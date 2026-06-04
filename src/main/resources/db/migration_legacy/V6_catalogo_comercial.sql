CREATE TABLE paquete (
    id                BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre            TEXT         NOT NULL,
    slug              TEXT         NOT NULL UNIQUE,
    descripcion_corta TEXT         NOT NULL,
    descripcion_larga TEXT,
    precio            NUMERIC(10,2) NOT NULL,
    badge             TEXT,
    color_hex         TEXT,
    imagen_path       TEXT,
    duracion_minutos  INT,
    limite_personas   INT,
    es_destacado      BOOLEAN      NOT NULL DEFAULT FALSE,
    es_activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    orden             INT          NOT NULL DEFAULT 0,

    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        UUID         REFERENCES perfil_usuario(id),
    updated_by        UUID         REFERENCES perfil_usuario(id),
    deleted_at        TIMESTAMPTZ,

    CONSTRAINT ck_paquete_precio    CHECK (precio > 0),
    CONSTRAINT ck_paquete_duracion  CHECK (duracion_minutos  IS NULL OR duracion_minutos  > 0),
    CONSTRAINT ck_paquete_limite    CHECK (limite_personas   IS NULL OR limite_personas   > 0),
    CONSTRAINT ck_paquete_color     CHECK (color_hex IS NULL OR color_hex ~ '^#[0-9A-Fa-f]{6}$')
);

CREATE INDEX idx_paquete_activo    ON paquete (es_activo, orden) WHERE deleted_at IS NULL;
CREATE INDEX idx_paquete_destacado ON paquete (es_destacado)     WHERE es_destacado = TRUE AND deleted_at IS NULL;

CREATE TRIGGER trg_paquete_updated_at
    BEFORE UPDATE ON paquete
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE paquete_beneficio (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    paquete_id      BIGINT       NOT NULL REFERENCES paquete(id) ON DELETE CASCADE,
    descripcion     TEXT         NOT NULL,
    orden           INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_beneficio_desc CHECK (length(descripcion) >= 3)
);

CREATE INDEX idx_paquete_beneficio_paquete ON paquete_beneficio (paquete_id, orden);


CREATE TABLE paquete_extra (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    paquete_id      BIGINT       NOT NULL REFERENCES paquete(id) ON DELETE CASCADE,
    nombre          TEXT         NOT NULL,
    descripcion     TEXT,
    es_activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden           INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_paquete_extra_paquete ON paquete_extra (paquete_id, orden) WHERE es_activo = TRUE;

CREATE TRIGGER trg_paquete_extra_updated_at
    BEFORE UPDATE ON paquete_extra
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE servicio_cotizacion (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre              TEXT         NOT NULL,
    descripcion         TEXT,
    precio_referencial  NUMERIC(10,2) NOT NULL DEFAULT 0,
    icono               TEXT,
    es_activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    orden               INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_servicio_precio CHECK (precio_referencial >= 0)
);

CREATE INDEX idx_servicio_activo ON servicio_cotizacion (es_activo, orden) WHERE es_activo = TRUE;

CREATE TRIGGER trg_servicio_cotizacion_updated_at
    BEFORE UPDATE ON servicio_cotizacion
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE promocion (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_codigo         TEXT         NOT NULL REFERENCES tipo_promocion(codigo) ON UPDATE CASCADE,
    sede_id             BIGINT       REFERENCES sede(id) ON DELETE CASCADE,
    nombre              TEXT         NOT NULL,
    descripcion         TEXT,
    valor_descuento     NUMERIC(10,2) NOT NULL DEFAULT 0,
    tipo_dia_codigo     TEXT         REFERENCES tipo_dia(codigo) ON UPDATE CASCADE,
    fecha_inicio        DATE         NOT NULL,
    fecha_fin           DATE,
    es_automatica       BOOLEAN      NOT NULL DEFAULT FALSE,
    es_activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    prioridad           INT          NOT NULL DEFAULT 0,
    minimo_personas     INT,
    monto_minimo        NUMERIC(10,2),
    limite_usos         INT,
    limite_por_cliente  INT,
    usos_actuales       INT          NOT NULL DEFAULT 0,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          UUID         REFERENCES perfil_usuario(id),
    updated_by          UUID         REFERENCES perfil_usuario(id),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_promocion_fechas       CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT ck_promocion_valor        CHECK (valor_descuento >= 0),
    CONSTRAINT ck_promocion_minimo_pers  CHECK (minimo_personas IS NULL OR minimo_personas > 0),
    CONSTRAINT ck_promocion_monto_min    CHECK (monto_minimo    IS NULL OR monto_minimo    >= 0),
    CONSTRAINT ck_promocion_limite       CHECK (limite_usos     IS NULL OR limite_usos     > 0),
    CONSTRAINT ck_promocion_limite_cli   CHECK (limite_por_cliente IS NULL OR limite_por_cliente > 0),
    CONSTRAINT ck_promocion_usos         CHECK (usos_actuales >= 0)
);

CREATE INDEX idx_promocion_activo_fechas ON promocion (es_activo, fecha_inicio, fecha_fin) WHERE deleted_at IS NULL;
CREATE INDEX idx_promocion_tipo_dia      ON promocion (tipo_dia_codigo) WHERE tipo_dia_codigo IS NOT NULL;
CREATE INDEX idx_promocion_prioridad     ON promocion (prioridad DESC);

CREATE TRIGGER trg_promocion_updated_at
    BEFORE UPDATE ON promocion
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE promocion_marketing (
    promocion_id              BIGINT       PRIMARY KEY REFERENCES promocion(id) ON DELETE CASCADE,
    imagen_path               TEXT,
    banner_path               TEXT,
    color_destacado           TEXT,
    texto_publicitario        TEXT,
    texto_boton               TEXT,
    url_boton                 TEXT,
    mostrar_en_inicio         BOOLEAN      NOT NULL DEFAULT FALSE,
    mostrar_en_carrusel       BOOLEAN      NOT NULL DEFAULT FALSE,
    mostrar_en_promociones    BOOLEAN      NOT NULL DEFAULT TRUE,
    mostrar_en_checkout       BOOLEAN      NOT NULL DEFAULT FALSE,
    solo_movil                BOOLEAN      NOT NULL DEFAULT FALSE,
    updated_at                TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_promo_mkt_color CHECK (color_destacado IS NULL OR color_destacado ~ '^#[0-9A-Fa-f]{6}$')
);

CREATE INDEX idx_promocion_marketing_inicio ON promocion_marketing (mostrar_en_inicio) WHERE mostrar_en_inicio = TRUE;

CREATE TRIGGER trg_promocion_marketing_updated_at
    BEFORE UPDATE ON promocion_marketing
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();