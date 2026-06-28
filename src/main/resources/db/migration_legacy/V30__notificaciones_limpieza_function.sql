CREATE OR REPLACE FUNCTION app.limpiar_notificaciones_expiradas()
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    n_leidas   INTEGER := 0;
    n_vencidas INTEGER := 0;
BEGIN
    DELETE FROM notificacion
    WHERE leida = TRUE
      AND expira_at IS NOT NULL
      AND expira_at < NOW();

    GET DIAGNOSTICS n_leidas = ROW_COUNT;

    DELETE FROM notificacion
    WHERE expira_at IS NOT NULL
      AND expira_at < NOW() - INTERVAL '7 days';

    GET DIAGNOSTICS n_vencidas = ROW_COUNT;

    RETURN n_leidas + n_vencidas;
END;
$$;


CREATE OR REPLACE FUNCTION app.limpiar_notificaciones_baja_prioridad(p_dias INTEGER DEFAULT 3)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    n INTEGER := 0;
BEGIN
    DELETE FROM notificacion
    WHERE prioridad = 'BAJA'
      AND leida = TRUE
      AND created_at < NOW() - (p_dias || ' days')::INTERVAL;

    GET DIAGNOSTICS n = ROW_COUNT;

    RETURN n;
END;
$$;
