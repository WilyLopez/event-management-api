CREATE TABLE zona_juego (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          TEXT         NOT NULL,
    slug            TEXT         NOT NULL UNIQUE,
    descripcion     TEXT         NOT NULL,
    edad_minima     INT,
    edad_maxima     INT,
    es_destacada    BOOLEAN      NOT NULL DEFAULT FALSE,
    es_activa       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden           INT          NOT NULL DEFAULT 0,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      UUID         REFERENCES perfil_usuario(id),
    updated_by      UUID         REFERENCES perfil_usuario(id),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_zona_edad_min   CHECK (edad_minima IS NULL OR edad_minima >= 0),
    CONSTRAINT ck_zona_edad_max   CHECK (edad_maxima IS NULL OR edad_maxima <= 17),
    CONSTRAINT ck_zona_edad_orden CHECK (edad_minima IS NULL OR edad_maxima IS NULL OR edad_maxima >= edad_minima)
);

CREATE INDEX idx_zona_juego_activa ON zona_juego (es_activa, orden) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_zona_juego_updated_at
    BEFORE UPDATE ON zona_juego
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE zona_juego_medio (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    zona_id         BIGINT       NOT NULL REFERENCES zona_juego(id) ON DELETE CASCADE,
    tipo            TEXT         NOT NULL,
    archivo_path    TEXT         NOT NULL,
    alt_texto       TEXT,
    orden           INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_zona_medio_tipo CHECK (tipo IN ('IMAGEN','VIDEO'))
);

CREATE INDEX idx_zona_juego_medio_zona ON zona_juego_medio (zona_id, orden);


CREATE TABLE actividad (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          TEXT         NOT NULL,
    descripcion     TEXT         NOT NULL,
    imagen_path     TEXT,
    zona_id         BIGINT       REFERENCES zona_juego(id) ON DELETE SET NULL,
    es_especial     BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_inicio    DATE,
    fecha_fin       DATE,
    es_destacada    BOOLEAN      NOT NULL DEFAULT FALSE,
    es_activa       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden           INT          NOT NULL DEFAULT 0,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      UUID         REFERENCES perfil_usuario(id),
    updated_by      UUID         REFERENCES perfil_usuario(id),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_actividad_fechas CHECK (fecha_fin IS NULL OR fecha_inicio IS NULL OR fecha_fin >= fecha_inicio)
);

CREATE INDEX idx_actividad_activa  ON actividad (es_activa, orden) WHERE deleted_at IS NULL;
CREATE INDEX idx_actividad_zona    ON actividad (zona_id)          WHERE zona_id IS NOT NULL;
CREATE INDEX idx_actividad_especial ON actividad (es_especial)     WHERE es_especial = TRUE;

CREATE TRIGGER trg_actividad_updated_at
    BEFORE UPDATE ON actividad
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE novedad (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo          TEXT         NOT NULL,
    descripcion     TEXT         NOT NULL,
    imagen_path     TEXT,
    texto_cta       TEXT,
    url_cta         TEXT,
    prioridad       INT          NOT NULL DEFAULT 0,
    fecha_inicio    DATE,
    fecha_fin       DATE,
    es_visible_home BOOLEAN      NOT NULL DEFAULT FALSE,
    es_destacada    BOOLEAN      NOT NULL DEFAULT FALSE,
    es_activa       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      UUID         REFERENCES perfil_usuario(id),
    updated_by      UUID         REFERENCES perfil_usuario(id),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_novedad_fechas CHECK (fecha_fin IS NULL OR fecha_inicio IS NULL OR fecha_fin >= fecha_inicio)
);

CREATE INDEX idx_novedad_home    ON novedad (es_visible_home, es_activa, prioridad) WHERE deleted_at IS NULL;
CREATE INDEX idx_novedad_destacada ON novedad (es_destacada) WHERE es_destacada = TRUE AND deleted_at IS NULL;

CREATE TRIGGER trg_novedad_updated_at
    BEFORE UPDATE ON novedad
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE banner (
    id                BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id           BIGINT       REFERENCES sede(id) ON DELETE CASCADE,
    titulo            TEXT         NOT NULL,
    descripcion       TEXT,
    imagen_path       TEXT         NOT NULL,
    imagen_movil_path TEXT,
    enlace_destino    TEXT,
    texto_boton       TEXT,
    color_overlay     TEXT,
    tipo              TEXT         NOT NULL DEFAULT 'HOME',
    fecha_inicio      DATE         NOT NULL,
    fecha_fin         DATE,
    es_activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    solo_movil        BOOLEAN      NOT NULL DEFAULT FALSE,
    solo_desktop      BOOLEAN      NOT NULL DEFAULT FALSE,
    orden             INT          NOT NULL DEFAULT 0,
    prioridad         INT          NOT NULL DEFAULT 0,

    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        UUID         REFERENCES perfil_usuario(id),
    updated_by        UUID         REFERENCES perfil_usuario(id),
    deleted_at        TIMESTAMPTZ,

    CONSTRAINT ck_banner_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT ck_banner_color  CHECK (color_overlay IS NULL OR color_overlay ~ '^#[0-9A-Fa-f]{6,8}$')
);

CREATE INDEX idx_banner_fechas ON banner (fecha_inicio, fecha_fin) WHERE deleted_at IS NULL;
CREATE INDEX idx_banner_activo ON banner (es_activo, orden, prioridad) WHERE es_activo = TRUE AND deleted_at IS NULL;
CREATE INDEX idx_banner_tipo   ON banner (tipo);

CREATE TRIGGER trg_banner_updated_at
    BEFORE UPDATE ON banner
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE galeria_imagen (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
    archivo_path        TEXT         NOT NULL,
    alt_texto           TEXT,
    titulo              TEXT,
    descripcion         TEXT,
    categoria           TEXT         NOT NULL,
    tipo_mime           TEXT,
    tamano_bytes        BIGINT,
    es_destacada        BOOLEAN      NOT NULL DEFAULT FALSE,
    es_activa           BOOLEAN      NOT NULL DEFAULT TRUE,
    orden               INT          NOT NULL DEFAULT 0,

    subida_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    subida_por          UUID         REFERENCES perfil_usuario(id),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_galeria_categoria CHECK (categoria IN ('CUMPLEANOS','JUEGOS','DECORACION','GENERAL','EVENTO'))
);

CREATE INDEX idx_galeria_sede_cat   ON galeria_imagen (sede_id, categoria) WHERE deleted_at IS NULL;
CREATE INDEX idx_galeria_destacada  ON galeria_imagen (es_destacada)       WHERE es_destacada = TRUE;
CREATE INDEX idx_galeria_orden      ON galeria_imagen (orden);

CREATE TRIGGER trg_galeria_updated_at
    BEFORE UPDATE ON galeria_imagen
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE faq (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pregunta        TEXT         NOT NULL,
    respuesta       TEXT         NOT NULL,
    orden           INT          NOT NULL DEFAULT 0,
    es_visible      BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by      UUID         REFERENCES perfil_usuario(id),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_faq_visible ON faq (es_visible, orden) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_faq_updated_at
    BEFORE UPDATE ON faq
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE contenido_legal (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo            TEXT         NOT NULL UNIQUE,
    titulo          TEXT         NOT NULL,
    contenido       TEXT         NOT NULL,
    version         INT          NOT NULL DEFAULT 1,
    es_activo       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by      UUID         REFERENCES perfil_usuario(id),

    CONSTRAINT ck_legal_tipo CHECK (tipo IN ('TERMINOS','PRIVACIDAD','REEMBOLSO','MENORES','COOKIES'))
);

CREATE TRIGGER trg_legal_updated_at
    BEFORE UPDATE ON contenido_legal
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE contenido_web (
    id                    BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    seccion_codigo        TEXT         NOT NULL REFERENCES seccion_web(codigo) ON UPDATE CASCADE,
    tipo_contenido_codigo TEXT         NOT NULL REFERENCES tipo_contenido(codigo) ON UPDATE CASCADE,
    clave                 TEXT         NOT NULL,
    valor_es              TEXT         NOT NULL,
    valor_en              TEXT,
    imagen_path           TEXT,
    descripcion           TEXT,
    metadatos             JSONB        NOT NULL DEFAULT '{}'::jsonb,
    version               INT          NOT NULL DEFAULT 1,
    es_visible            BOOLEAN      NOT NULL DEFAULT TRUE,
    orden                 INT          NOT NULL DEFAULT 0,

    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by            UUID         REFERENCES perfil_usuario(id),
    deleted_at            TIMESTAMPTZ,

    CONSTRAINT uk_contenido_web UNIQUE (seccion_codigo, clave)
);

CREATE INDEX idx_contenido_web_seccion ON contenido_web (seccion_codigo, es_visible) WHERE deleted_at IS NULL;
CREATE INDEX idx_contenido_web_clave   ON contenido_web (clave);

CREATE TRIGGER trg_contenido_web_updated_at
    BEFORE UPDATE ON contenido_web
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE mensaje_contacto (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          TEXT         NOT NULL,
    correo          CITEXT       NOT NULL,
    telefono        TEXT,
    asunto          TEXT,
    mensaje         TEXT         NOT NULL,
    estado          TEXT         NOT NULL DEFAULT 'PENDIENTE',
    respuesta       TEXT,
    respondido_por  UUID         REFERENCES perfil_usuario(id),
    respondido_at   TIMESTAMPTZ,
    ip_origen       INET,
    user_agent      TEXT,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_mensaje_estado CHECK (estado IN ('PENDIENTE','LEIDO','RESPONDIDO','SPAM','ARCHIVADO'))
);

CREATE INDEX idx_mensaje_estado ON mensaje_contacto (estado, created_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_mensaje_correo ON mensaje_contacto (correo);

CREATE TRIGGER trg_mensaje_contacto_updated_at
    BEFORE UPDATE ON mensaje_contacto
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE resena (
    id                BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id        BIGINT       REFERENCES cliente_perfil(id) ON DELETE SET NULL,
    evento_id         BIGINT,
    nombre_autor      TEXT         NOT NULL,
    contenido         TEXT         NOT NULL,
    calificacion      INT          NOT NULL,
    foto_path         TEXT,
    es_aprobada       BOOLEAN      NOT NULL DEFAULT FALSE,
    es_destacada      BOOLEAN      NOT NULL DEFAULT FALSE,
    mostrar_home      BOOLEAN      NOT NULL DEFAULT TRUE,
    respuesta_admin   TEXT,
    respondida_at     TIMESTAMPTZ,
    aprobada_por      UUID         REFERENCES perfil_usuario(id),
    aprobada_at       TIMESTAMPTZ,

    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted_at        TIMESTAMPTZ,

    CONSTRAINT ck_resena_calificacion CHECK (calificacion BETWEEN 1 AND 5)
);

CREATE INDEX idx_resena_aprobada  ON resena (es_aprobada, created_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_resena_destacada ON resena (es_destacada) WHERE es_destacada = TRUE AND deleted_at IS NULL;
CREATE INDEX idx_resena_cliente   ON resena (cliente_id)   WHERE cliente_id IS NOT NULL;

CREATE TRIGGER trg_resena_updated_at
    BEFORE UPDATE ON resena
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();