-- Fix: las funciones de trigger de `reserva` aún referenciaban la tabla
-- `configuracion_sede`, eliminada en Fase 6 (V_fase6_drop_configuracion_sede.sql).
-- La configuración vive ahora en `configuracion_calendario` (V15), con `idsede`
-- en lugar de `sede_id`. PL/pgSQL compila el cuerpo de forma diferida, por eso
-- el error "relation configuracion_sede does not exist" solo aparecía al insertar
-- una reserva (al dispararse el trigger), no al crear la función.

-- 1) BEFORE INSERT/UPDATE en reserva: validación de fecha, aforo y horario.
CREATE OR REPLACE FUNCTION app.validar_reserva_publica()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cfg          configuracion_calendario%ROWTYPE;
    v_aforo_actual INT;
    v_hoy          DATE := app.hoy();
    v_dias_anticip INT;
BEGIN
    SELECT * INTO v_cfg FROM configuracion_calendario WHERE idsede = NEW.sede_id;
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

    -- NOTA: un feriado NO bloquea la venta de entradas. Un feriado solo determina
    -- la tarifa (tipo_dia = FIN_SEMANA_FERIADO). Para cerrar un dia hay que crear un
    -- bloqueo en bloque_calendario (dia_esta_bloqueado). Por eso aqui ya no se valida
    -- es_feriado: feriado y dia bloqueado son conceptos distintos.

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


-- 2) AFTER INSERT/UPDATE en reserva: notificacion de aforo cercano al limite.
CREATE OR REPLACE FUNCTION app.detectar_aforo_limite()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cfg          configuracion_calendario%ROWTYPE;
    v_aforo_actual INT;
    v_admin        RECORD;
BEGIN
    IF NEW.estado_codigo NOT IN ('CONFIRMADA', 'PENDIENTE') THEN
        RETURN NEW;
    END IF;

    SELECT * INTO v_cfg FROM configuracion_calendario WHERE idsede = NEW.sede_id;
    v_aforo_actual := app.aforo_ocupado(NEW.sede_id, NEW.fecha_evento);

    IF v_aforo_actual >= (v_cfg.aforo_maximo * 0.9)::INT
       AND v_aforo_actual < v_cfg.aforo_maximo
       AND NEW.fecha_evento BETWEEN app.hoy() AND app.hoy() + INTERVAL '3 days' THEN
        FOR v_admin IN
            SELECT DISTINCT ur.usuario_id
            FROM usuario_rol ur
            WHERE ur.rol_codigo IN ('SUPERADMIN', 'ADMIN')
        LOOP
            IF NOT EXISTS (
                SELECT 1 FROM notificacion
                WHERE tipo_codigo = 'AFORO_LIMITE'
                  AND entidad_id = NEW.id
                  AND destinatario_usuario_id = v_admin.usuario_id
                  AND created_at > (NOW() - INTERVAL '24 hours')
            ) THEN
                PERFORM app.notificar_evento(
                    'AFORO_LIMITE',
                    v_admin.usuario_id, NULL,
                    'RESERVA', NEW.id,
                    'Aforo cercano al limite',
                    'Aforo al ' || ((v_aforo_actual::FLOAT / v_cfg.aforo_maximo) * 100)::INT || '% para ' || to_char(NEW.fecha_evento, 'DD/MM/YYYY'),
                    '/admin/calendario?fecha=' || to_char(NEW.fecha_evento, 'YYYY-MM-DD')
                );
            END IF;
        END LOOP;
    END IF;
    RETURN NEW;
END;
$$;
