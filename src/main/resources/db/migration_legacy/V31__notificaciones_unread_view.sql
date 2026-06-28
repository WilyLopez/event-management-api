CREATE OR REPLACE VIEW v_notif_no_leidas AS
SELECT
    destinatario_usuario_id,
    destinatario_cliente_id,
    COUNT(*)::BIGINT AS count_no_leidas
FROM notificacion
WHERE leida = FALSE
  AND (expira_at IS NULL OR expira_at > NOW())
GROUP BY destinatario_usuario_id, destinatario_cliente_id;


CREATE OR REPLACE FUNCTION app.contar_no_leidas_usuario(p_usuario_id UUID)
RETURNS BIGINT
LANGUAGE sql
STABLE
AS $$
    SELECT COUNT(*)
    FROM notificacion
    WHERE destinatario_usuario_id = p_usuario_id
      AND leida = FALSE
      AND (expira_at IS NULL OR expira_at > NOW());
$$;


CREATE OR REPLACE FUNCTION app.contar_no_leidas_cliente(p_cliente_id BIGINT)
RETURNS BIGINT
LANGUAGE sql
STABLE
AS $$
    SELECT COUNT(*)
    FROM notificacion
    WHERE destinatario_cliente_id = p_cliente_id
      AND leida = FALSE
      AND (expira_at IS NULL OR expira_at > NOW());
$$;
