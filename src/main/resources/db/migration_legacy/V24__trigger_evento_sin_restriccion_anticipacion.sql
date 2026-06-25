-- Elimina la validación de días de anticipación (mín/máx) del trigger de evento privado.
-- Estas reglas de negocio se aplican en la capa de aplicación (EventoPrivadoService),
-- donde el admin puede bypassearlas. El trigger conserva solo constraints de integridad:
-- fecha pasada, turno ocupado, reservas públicas, bloqueo y feriado.

CREATE OR REPLACE FUNCTION app.validar_evento_privado()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_hoy DATE := app.hoy();
BEGIN
    IF NEW.fecha_evento < v_hoy THEN
        RAISE EXCEPTION 'No se puede crear evento en fechas pasadas (fecha: %)', NEW.fecha_evento
            USING ERRCODE = 'check_violation';
    END IF;

    IF NEW.estado_codigo IN ('SOLICITADA', 'CONFIRMADA', 'EN_CURSO') THEN
        IF app.dia_tiene_reserva_publica(NEW.sede_id, NEW.fecha_evento) THEN
            RAISE EXCEPTION 'La fecha ya tiene reservas publicas y no admite eventos privados'
                USING ERRCODE = 'check_violation';
        END IF;

        IF app.dia_esta_bloqueado(NEW.sede_id, NEW.fecha_evento, NEW.turno_codigo) THEN
            RAISE EXCEPTION 'La fecha o turno esta bloqueado'
                USING ERRCODE = 'check_violation';
        END IF;

        IF app.es_feriado(NEW.sede_id, NEW.fecha_evento) THEN
            RAISE EXCEPTION 'La fecha es feriado'
                USING ERRCODE = 'check_violation';
        END IF;

        IF EXISTS (
            SELECT 1 FROM evento e
            WHERE e.sede_id   = NEW.sede_id
              AND e.fecha_evento = NEW.fecha_evento
              AND e.turno_codigo = NEW.turno_codigo
              AND e.estado_codigo IN ('SOLICITADA', 'CONFIRMADA', 'EN_CURSO')
              AND e.deleted_at IS NULL
              AND e.id <> COALESCE(NEW.id, -1)
        ) THEN
            RAISE EXCEPTION 'El turno % ya tiene un evento privado para la fecha %',
                NEW.turno_codigo, NEW.fecha_evento
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;

    RETURN NEW;
END;
$$;
