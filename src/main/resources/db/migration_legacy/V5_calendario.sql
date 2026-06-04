CREATE TABLE feriado (
    id            BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id       BIGINT       REFERENCES sede(id) ON DELETE CASCADE,
    tipo_codigo   TEXT         NOT NULL REFERENCES tipo_feriado(codigo) ON UPDATE CASCADE,
    fecha         DATE         NOT NULL,
    descripcion   TEXT         NOT NULL,
    anio          INT          GENERATED ALWAYS AS (EXTRACT(YEAR FROM fecha)::INT) STORED,

    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    UUID         REFERENCES perfil_usuario(id),
    updated_by    UUID         REFERENCES perfil_usuario(id),
    deleted_at    TIMESTAMPTZ,

    CONSTRAINT uk_feriado_fecha_sede UNIQUE NULLS NOT DISTINCT (fecha, sede_id),
    CONSTRAINT ck_feriado_descripcion CHECK (length(descripcion) >= 3)
);

CREATE INDEX idx_feriado_fecha ON feriado (fecha) WHERE deleted_at IS NULL;
CREATE INDEX idx_feriado_anio  ON feriado (anio);
CREATE INDEX idx_feriado_sede  ON feriado (sede_id) WHERE sede_id IS NOT NULL;

CREATE TRIGGER trg_feriado_updated_at
    BEFORE UPDATE ON feriado
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE bloque_calendario (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id         BIGINT       NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
    fecha_inicio    DATE         NOT NULL,
    fecha_fin       DATE         NOT NULL,
    turno_codigo    TEXT         REFERENCES turno(codigo) ON UPDATE CASCADE,
    motivo          TEXT         NOT NULL,
    es_activo       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      UUID         REFERENCES perfil_usuario(id),
    updated_by      UUID         REFERENCES perfil_usuario(id),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_bloque_fechas  CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT ck_bloque_motivo  CHECK (length(motivo) >= 3)
);

CREATE INDEX idx_bloque_sede_fechas
    ON bloque_calendario (sede_id, fecha_inicio, fecha_fin)
    WHERE deleted_at IS NULL AND es_activo = TRUE;
CREATE INDEX idx_bloque_turno
    ON bloque_calendario (turno_codigo)
    WHERE turno_codigo IS NOT NULL;

CREATE TRIGGER trg_bloque_updated_at
    BEFORE UPDATE ON bloque_calendario
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE tarifa (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id         BIGINT       NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
    tipo_dia_codigo TEXT         NOT NULL REFERENCES tipo_dia(codigo) ON UPDATE CASCADE,
    precio          NUMERIC(10,2) NOT NULL,
    vigencia_desde  DATE         NOT NULL,
    vigencia_hasta  DATE,
    es_activo       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      UUID         REFERENCES perfil_usuario(id),
    updated_by      UUID         REFERENCES perfil_usuario(id),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_tarifa_precio   CHECK (precio >= 0),
    CONSTRAINT ck_tarifa_vigencia CHECK (vigencia_hasta IS NULL OR vigencia_hasta >= vigencia_desde),
    CONSTRAINT uk_tarifa_inicio   UNIQUE (sede_id, tipo_dia_codigo, vigencia_desde)
);

CREATE INDEX idx_tarifa_sede_tipo
    ON tarifa (sede_id, tipo_dia_codigo)
    WHERE deleted_at IS NULL AND es_activo = TRUE;
CREATE INDEX idx_tarifa_vigencia ON tarifa (vigencia_desde, vigencia_hasta);

CREATE TRIGGER trg_tarifa_updated_at
    BEFORE UPDATE ON tarifa
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();