CREATE TABLE serie_comprobante (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
    tipo_comp_codigo    TEXT         NOT NULL REFERENCES tipo_comprobante(codigo) ON UPDATE CASCADE,
    serie               TEXT         NOT NULL,
    correlativo_actual  BIGINT       NOT NULL DEFAULT 0,
    es_activa           BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_serie_sede     UNIQUE (sede_id, serie),
    CONSTRAINT ck_serie_formato  CHECK (serie ~ '^[A-Z][0-9]{3}$'),
    CONSTRAINT ck_serie_correlativo CHECK (correlativo_actual >= 0)
);

CREATE INDEX idx_serie_comprobante_sede ON serie_comprobante (sede_id, tipo_comp_codigo) WHERE es_activa = TRUE;

CREATE TRIGGER trg_serie_comprobante_updated_at
    BEFORE UPDATE ON serie_comprobante
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE OR REPLACE FUNCTION app.obtener_siguiente_correlativo(
    p_sede_id BIGINT,
    p_tipo_comp_codigo TEXT
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_correlativo BIGINT;
BEGIN
    UPDATE serie_comprobante
    SET correlativo_actual = correlativo_actual + 1,
        updated_at = NOW()
    WHERE sede_id = p_sede_id
      AND tipo_comp_codigo = p_tipo_comp_codigo
      AND es_activa = TRUE
    RETURNING correlativo_actual INTO v_correlativo;

    IF v_correlativo IS NULL THEN
        RAISE EXCEPTION 'No existe una serie activa para sede % y tipo de comprobante %',
            p_sede_id, p_tipo_comp_codigo;
    END IF;

    RETURN v_correlativo;
END;
$$;


CREATE TABLE comprobante (
    id                          BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    serie_comprobante_id        BIGINT       NOT NULL REFERENCES serie_comprobante(id) ON DELETE RESTRICT,
    tipo_comp_codigo            TEXT         NOT NULL REFERENCES tipo_comprobante(codigo) ON UPDATE CASCADE,
    estado_codigo               TEXT         NOT NULL DEFAULT 'PENDIENTE' REFERENCES estado_comprobante(codigo) ON UPDATE CASCADE,

    venta_id                    BIGINT       REFERENCES venta(id) ON DELETE RESTRICT,
    evento_id                   BIGINT       REFERENCES evento(id) ON DELETE RESTRICT,

    serie                       TEXT         NOT NULL,
    correlativo                 BIGINT       NOT NULL,
    numero_completo             TEXT         NOT NULL,

    ruc_emisor                  TEXT         NOT NULL,
    razon_social_emisor         TEXT         NOT NULL,

    tipo_doc_receptor_codigo    TEXT         NOT NULL REFERENCES tipo_documento(codigo) ON UPDATE CASCADE,
    numero_doc_receptor         TEXT,
    razon_social_receptor       TEXT,
    direccion_receptor          TEXT,
    correo_receptor             CITEXT,

    monto_base                  NUMERIC(10,2) NOT NULL,
    monto_igv                   NUMERIC(10,2) NOT NULL DEFAULT 0,
    monto_total                 NUMERIC(10,2) NOT NULL,
    moneda                      TEXT         NOT NULL DEFAULT 'PEN',

    xml_path                    TEXT,
    pdf_path                    TEXT,
    cdr_path                    TEXT,
    hash_documento              TEXT,
    cdr_estado                  TEXT,
    cdr_descripcion             TEXT,

    proveedor_sunat             TEXT         NOT NULL DEFAULT 'NUBEFACT',
    proveedor_response          JSONB,

    motivo_anulacion            TEXT,
    comprobante_anulado_id      BIGINT       REFERENCES comprobante(id) ON DELETE SET NULL,

    fecha_emision               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fecha_envio_sunat           TIMESTAMPTZ,
    fecha_respuesta_sunat       TIMESTAMPTZ,

    created_at                  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by                  UUID         REFERENCES perfil_usuario(id),

    CONSTRAINT uk_comprobante_numero    UNIQUE (numero_completo),
    CONSTRAINT uk_comprobante_serie_cor UNIQUE (serie_comprobante_id, correlativo),
    CONSTRAINT ck_comprobante_ruc       CHECK (length(ruc_emisor) = 11),
    CONSTRAINT ck_comprobante_montos    CHECK (monto_base >= 0 AND monto_igv >= 0 AND monto_total >= 0),
    CONSTRAINT ck_comprobante_total     CHECK (monto_total = monto_base + monto_igv),
    CONSTRAINT ck_comprobante_origen    CHECK (
        (venta_id IS NOT NULL AND evento_id IS NULL)
        OR (venta_id IS NULL AND evento_id IS NOT NULL)
        OR (tipo_comp_codigo = 'NOTA_CREDITO' AND comprobante_anulado_id IS NOT NULL)
    )
);

CREATE INDEX idx_comprobante_venta       ON comprobante (venta_id)         WHERE venta_id  IS NOT NULL;
CREATE INDEX idx_comprobante_evento      ON comprobante (evento_id)        WHERE evento_id IS NOT NULL;
CREATE INDEX idx_comprobante_estado      ON comprobante (estado_codigo);
CREATE INDEX idx_comprobante_fecha       ON comprobante (fecha_emision DESC);
CREATE INDEX idx_comprobante_receptor    ON comprobante (numero_doc_receptor) WHERE numero_doc_receptor IS NOT NULL;
CREATE INDEX idx_comprobante_anulado     ON comprobante (comprobante_anulado_id) WHERE comprobante_anulado_id IS NOT NULL;

CREATE TRIGGER trg_comprobante_updated_at
    BEFORE UPDATE ON comprobante
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();