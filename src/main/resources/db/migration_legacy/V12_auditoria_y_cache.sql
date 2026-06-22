CREATE TABLE auditoria_log (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY,
    usuario_id          UUID         REFERENCES perfil_usuario(id) ON DELETE SET NULL,
    accion              TEXT         NOT NULL,
    modulo              TEXT         NOT NULL,
    entidad             TEXT         NOT NULL,
    entidad_id          BIGINT,
    valor_anterior      JSONB,
    valor_nuevo         JSONB,
    descripcion         TEXT,
    ip_origen           INET,
    user_agent          TEXT,
    nivel               TEXT         NOT NULL DEFAULT 'INFO',
    resultado           TEXT         NOT NULL DEFAULT 'EXITOSO',
    metadata            JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    PRIMARY KEY (id, created_at),
    CONSTRAINT ck_auditoria_nivel     CHECK (nivel     IN ('INFO','WARNING','ERROR','CRITICAL')),
    CONSTRAINT ck_auditoria_resultado CHECK (resultado IN ('EXITOSO','FALLIDO','PARCIAL'))
) PARTITION BY RANGE (created_at);


CREATE TABLE auditoria_log_default PARTITION OF auditoria_log DEFAULT;


CREATE OR REPLACE FUNCTION app.crear_particion_auditoria(p_anio INT, p_mes INT)
RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    v_nombre TEXT;
    v_desde  TEXT;
    v_hasta  TEXT;
BEGIN
    v_nombre := format('auditoria_log_%s%s', p_anio, lpad(p_mes::text, 2, '0'));
    v_desde  := format('%s-%s-01', p_anio, lpad(p_mes::text, 2, '0'));
    v_hasta  := to_char(
        (v_desde::date + interval '1 month'),
        'YYYY-MM-DD'
    );

    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF auditoria_log FOR VALUES FROM (%L) TO (%L)',
        v_nombre, v_desde, v_hasta
    );

    EXECUTE format('CREATE INDEX IF NOT EXISTS idx_%s_usuario ON %I (usuario_id, created_at DESC)',
        v_nombre, v_nombre);
    EXECUTE format('CREATE INDEX IF NOT EXISTS idx_%s_entidad ON %I (entidad, entidad_id)',
        v_nombre, v_nombre);
    EXECUTE format('CREATE INDEX IF NOT EXISTS idx_%s_modulo ON %I (modulo, nivel)',
        v_nombre, v_nombre);
END;
$$;


DO $$
DECLARE
    v_fecha DATE := date_trunc('month', NOW())::date;
    v_anio  INT;
    v_mes   INT;
    i INT;
BEGIN
    FOR i IN 0..11 LOOP
        v_anio := EXTRACT(YEAR FROM v_fecha + (i || ' months')::interval)::int;
        v_mes  := EXTRACT(MONTH FROM v_fecha + (i || ' months')::interval)::int;
        PERFORM app.crear_particion_auditoria(v_anio, v_mes);
    END LOOP;
END$$;


CREATE OR REPLACE FUNCTION app.registrar_auditoria(
    p_accion          TEXT,
    p_modulo          TEXT,
    p_entidad         TEXT,
    p_entidad_id      BIGINT DEFAULT NULL,
    p_valor_anterior  JSONB  DEFAULT NULL,
    p_valor_nuevo     JSONB  DEFAULT NULL,
    p_descripcion     TEXT   DEFAULT NULL,
    p_nivel           TEXT   DEFAULT 'INFO',
    p_resultado       TEXT   DEFAULT 'EXITOSO'
)
RETURNS BIGINT
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_id BIGINT;
BEGIN
    INSERT INTO auditoria_log (
        usuario_id, accion, modulo, entidad, entidad_id,
        valor_anterior, valor_nuevo, descripcion, nivel, resultado
    ) VALUES (
        auth.uid(), p_accion, p_modulo, p_entidad, p_entidad_id,
        p_valor_anterior, p_valor_nuevo, p_descripcion, p_nivel, p_resultado
    )
    RETURNING id INTO v_id;
    RETURN v_id;
END;
$$;


CREATE TABLE cliente_token (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id          BIGINT       NOT NULL REFERENCES cliente_perfil(id) ON DELETE CASCADE,
    token_hash          TEXT         NOT NULL UNIQUE,
    tipo                TEXT         NOT NULL,
    expira_at           TIMESTAMPTZ  NOT NULL,
    usado_at            TIMESTAMPTZ,
    metadata            JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_cliente_token_tipo CHECK (tipo IN ('VINCULAR_CUENTA','INVITACION','VERIFICAR_CORREO'))
);

CREATE INDEX idx_cliente_token_cliente ON cliente_token (cliente_id);
CREATE INDEX idx_cliente_token_expira  ON cliente_token (expira_at) WHERE usado_at IS NULL;