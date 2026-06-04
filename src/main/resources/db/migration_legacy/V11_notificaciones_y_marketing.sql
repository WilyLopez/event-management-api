CREATE TABLE notificacion (
    id                      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_codigo             TEXT         NOT NULL REFERENCES tipo_notificacion(codigo) ON UPDATE CASCADE,

    destinatario_usuario_id UUID         REFERENCES perfil_usuario(id) ON DELETE CASCADE,
    destinatario_cliente_id BIGINT       REFERENCES cliente_perfil(id) ON DELETE CASCADE,

    entidad_tipo            TEXT,
    entidad_id              BIGINT,

    titulo                  TEXT         NOT NULL,
    mensaje                 TEXT         NOT NULL,
    url_accion              TEXT,
    metadata                JSONB        NOT NULL DEFAULT '{}'::jsonb,

    leida                   BOOLEAN      NOT NULL DEFAULT FALSE,
    leida_at                TIMESTAMPTZ,
    prioridad               TEXT         NOT NULL DEFAULT 'NORMAL',
    expira_at               TIMESTAMPTZ,

    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_notif_destinatario CHECK (
        (destinatario_usuario_id IS NOT NULL AND destinatario_cliente_id IS NULL)
        OR (destinatario_usuario_id IS NULL AND destinatario_cliente_id IS NOT NULL)
    ),
    CONSTRAINT ck_notif_prioridad    CHECK (prioridad IN ('BAJA','NORMAL','ALTA','CRITICA'))
);

CREATE INDEX idx_notif_usuario_feed
    ON notificacion (destinatario_usuario_id, leida, created_at DESC)
    WHERE destinatario_usuario_id IS NOT NULL;

CREATE INDEX idx_notif_cliente_feed
    ON notificacion (destinatario_cliente_id, leida, created_at DESC)
    WHERE destinatario_cliente_id IS NOT NULL;

CREATE INDEX idx_notif_tipo     ON notificacion (tipo_codigo);
CREATE INDEX idx_notif_entidad  ON notificacion (entidad_tipo, entidad_id) WHERE entidad_tipo IS NOT NULL;
CREATE INDEX idx_notif_expira   ON notificacion (expira_at) WHERE expira_at IS NOT NULL;


CREATE TABLE notificacion_entrega (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    notificacion_id     BIGINT       NOT NULL REFERENCES notificacion(id) ON DELETE CASCADE,
    canal               TEXT         NOT NULL,
    estado              TEXT         NOT NULL DEFAULT 'PENDIENTE',
    intentos            INT          NOT NULL DEFAULT 0,
    enviado_at          TIMESTAMPTZ,
    mensaje_error       TEXT,
    proveedor_id        TEXT,
    metadata            JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_entrega_canal  CHECK (canal IN ('IN_APP','EMAIL','WHATSAPP','SMS','PUSH')),
    CONSTRAINT ck_entrega_estado CHECK (estado IN ('PENDIENTE','ENVIANDO','ENVIADO','ERROR','REBOTADO','CANCELADO')),
    CONSTRAINT ck_entrega_intentos CHECK (intentos >= 0)
);

CREATE INDEX idx_entrega_notificacion ON notificacion_entrega (notificacion_id);
CREATE INDEX idx_entrega_estado       ON notificacion_entrega (estado, canal) WHERE estado IN ('PENDIENTE','ENVIANDO','ERROR');


CREATE TABLE preferencia_notificacion (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id          UUID         REFERENCES perfil_usuario(id) ON DELETE CASCADE,
    cliente_id          BIGINT       REFERENCES cliente_perfil(id) ON DELETE CASCADE,
    tipo_codigo         TEXT         NOT NULL REFERENCES tipo_notificacion(codigo) ON UPDATE CASCADE,
    canales             TEXT[]       NOT NULL,
    es_activa           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_pref_notif_dest CHECK (
        (usuario_id IS NOT NULL AND cliente_id IS NULL)
        OR (usuario_id IS NULL AND cliente_id IS NOT NULL)
    ),
    CONSTRAINT uk_pref_notif_usuario UNIQUE (usuario_id, tipo_codigo),
    CONSTRAINT uk_pref_notif_cliente UNIQUE (cliente_id, tipo_codigo)
);

CREATE TRIGGER trg_preferencia_notificacion_updated_at
    BEFORE UPDATE ON preferencia_notificacion
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE plantilla_email (
    id                      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_email_codigo       TEXT         NOT NULL REFERENCES tipo_email(codigo) ON UPDATE CASCADE,
    nombre                  TEXT         NOT NULL,
    asunto                  TEXT         NOT NULL,
    contenido_html          TEXT         NOT NULL,
    contenido_fallback      TEXT,
    variables_permitidas    JSONB        NOT NULL DEFAULT '[]'::jsonb,
    es_activa               BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by              UUID         REFERENCES perfil_usuario(id),
    updated_by              UUID         REFERENCES perfil_usuario(id),
    deleted_at              TIMESTAMPTZ
);

CREATE INDEX idx_plantilla_email_tipo ON plantilla_email (tipo_email_codigo) WHERE es_activa = TRUE AND deleted_at IS NULL;

CREATE TRIGGER trg_plantilla_email_updated_at
    BEFORE UPDATE ON plantilla_email
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE campana_email (
    id                      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    plantilla_id            BIGINT       NOT NULL REFERENCES plantilla_email(id) ON DELETE RESTRICT,
    nombre                  TEXT         NOT NULL,
    descripcion             TEXT,
    estado                  TEXT         NOT NULL DEFAULT 'BORRADOR',
    fecha_programada        TIMESTAMPTZ,
    total_destinatarios     INT          NOT NULL DEFAULT 0,
    total_enviados          INT          NOT NULL DEFAULT 0,
    total_fallidos          INT          NOT NULL DEFAULT 0,
    filtros                 JSONB        NOT NULL DEFAULT '{}'::jsonb,

    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by              UUID         NOT NULL REFERENCES perfil_usuario(id),
    enviada_at              TIMESTAMPTZ,
    enviada_por             UUID         REFERENCES perfil_usuario(id),
    deleted_at              TIMESTAMPTZ,

    CONSTRAINT ck_campana_estado CHECK (estado IN ('BORRADOR','PROGRAMADA','ENVIANDO','FINALIZADA','CANCELADA')),
    CONSTRAINT ck_campana_totales CHECK (
        total_destinatarios >= 0 AND total_enviados >= 0 AND total_fallidos >= 0
    )
);

CREATE INDEX idx_campana_email_estado ON campana_email (estado, fecha_programada) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_campana_email_updated_at
    BEFORE UPDATE ON campana_email
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE envio_email (
    id                      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    campana_id              BIGINT       REFERENCES campana_email(id) ON DELETE SET NULL,
    plantilla_id            BIGINT       REFERENCES plantilla_email(id) ON DELETE SET NULL,
    cliente_id              BIGINT       REFERENCES cliente_perfil(id) ON DELETE SET NULL,
    usuario_id              UUID         REFERENCES perfil_usuario(id) ON DELETE SET NULL,

    destinatario            CITEXT       NOT NULL,
    asunto                  TEXT         NOT NULL,
    estado                  TEXT         NOT NULL DEFAULT 'PENDIENTE',
    intentos                INT          NOT NULL DEFAULT 0,
    enviado_at              TIMESTAMPTZ,
    mensaje_error           TEXT,
    proveedor_mensaje_id    TEXT,
    metadata                JSONB        NOT NULL DEFAULT '{}'::jsonb,

    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_envio_estado CHECK (estado IN ('PENDIENTE','ENVIADO','ERROR','REBOTADO','CANCELADO'))
);

CREATE INDEX idx_envio_email_campana    ON envio_email (campana_id)  WHERE campana_id  IS NOT NULL;
CREATE INDEX idx_envio_email_cliente    ON envio_email (cliente_id)  WHERE cliente_id  IS NOT NULL;
CREATE INDEX idx_envio_email_estado     ON envio_email (estado);
CREATE INDEX idx_envio_email_destinatario ON envio_email (destinatario, created_at DESC);