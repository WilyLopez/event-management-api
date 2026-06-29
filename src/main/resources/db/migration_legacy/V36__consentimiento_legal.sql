CREATE TABLE IF NOT EXISTS consentimiento_legal (
    id            BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    origen        VARCHAR(20)  NOT NULL,
    referencia_id BIGINT,
    tipo          VARCHAR(50)  NOT NULL REFERENCES tipo_legal (codigo),
    version_v     INT          NOT NULL,
    ip            VARCHAR(64),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_consentimiento_legal_ref
    ON consentimiento_legal (origen, referencia_id);

CREATE INDEX IF NOT EXISTS idx_consentimiento_legal_tipo
    ON consentimiento_legal (tipo, version_v);
