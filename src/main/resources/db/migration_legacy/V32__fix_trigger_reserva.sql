-- Fix: FOR UPDATE is not allowed with aggregate functions
-- Replaces app.validar_reserva_publica() removing the invalid FOR UPDATE clause

CREATE OR REPLACE FUNCTION app.validar_reserva_publica()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cfg          configuracion_sede%ROWTYPE;
    v_aforo_actual INT;
    v_hoy          DATE := app.hoy();
    v_dias_anticip INT;
BEGIN
    SELECT * INTO v_cfg FROM configuracion_sede WHERE sede_id = NEW.sede_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No hay configuracion de calendario para la sede %', NEW.sede_id;
    END IF;

    IF NEW.fecha_evento < v_hoy THEN
        RAISE EXCEPTION 'No se puede reservar en fechas pasadas (fecha: %)', NEW.fecha_evento
            USING ERRCODE = 'check_violation';
    END IF;

    v_dias_anticip := NEW.fecha_evento - v_hoy;
    IF v_dias_anticip > v_cfg.dias_max_reserva_publica THEN
        RAISE EXCEPTION 'La fecha excede el limite de % dias de anticipacion', v_cfg.dias_max_reserva_publica
            USING ERRCODE = 'check_violation';
    END IF;

    IF NEW.fecha_evento = v_hoy AND (app.ahora() AT TIME ZONE 'America/Lima')::TIME > v_cfg.hora_cierre THEN
        RAISE EXCEPTION 'El local ya cerro por hoy. Hora actual supera %', v_cfg.hora_cierre
            USING ERRCODE = 'check_violation';
    END IF;

    IF app.dia_tiene_evento_privado(NEW.sede_id, NEW.fecha_evento) THEN
        RAISE EXCEPTION 'La fecha tiene un evento privado y no admite reservas publicas'
            USING ERRCODE = 'check_violation';
    END IF;

    IF app.dia_esta_bloqueado(NEW.sede_id, NEW.fecha_evento) THEN
        RAISE EXCEPTION 'La fecha esta bloqueada'
            USING ERRCODE = 'check_violation';
    END IF;

    IF app.es_feriado(NEW.sede_id, NEW.fecha_evento) THEN
        RAISE EXCEPTION 'La fecha es feriado'
            USING ERRCODE = 'check_violation';
    END IF;

    IF NEW.estado_codigo IN ('PENDIENTE', 'CONFIRMADA', 'REPROGRAMADA') THEN
        SELECT COUNT(*)::INT INTO v_aforo_actual
        FROM reserva r
        WHERE r.sede_id = NEW.sede_id
          AND r.fecha_evento = NEW.fecha_evento
          AND r.estado_codigo IN ('PENDIENTE', 'CONFIRMADA', 'REPROGRAMADA')
          AND r.deleted_at IS NULL
          AND r.id <> COALESCE(NEW.id, -1);

        IF v_aforo_actual + 1 > v_cfg.aforo_maximo THEN
            RAISE EXCEPTION 'Aforo maximo (%) alcanzado para la fecha %', v_cfg.aforo_maximo, NEW.fecha_evento
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;

    NEW.tipo_dia_codigo := COALESCE(NEW.tipo_dia_codigo, app.tipo_dia_de_fecha(NEW.sede_id, NEW.fecha_evento));

    RETURN NEW;
END;
$$;
