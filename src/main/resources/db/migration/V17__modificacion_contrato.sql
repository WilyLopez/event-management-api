 ALTER TABLE contrato
    ADD COLUMN IF NOT EXISTS version         INT          NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS plantilla       VARCHAR(60),
    ADD COLUMN IF NOT EXISTS observaciones   TEXT;

UPDATE contrato SET idestado = 'BORRADOR' WHERE idestado NOT IN (
    'BORRADOR','ENVIADO','PENDIENTE_FIRMA','FIRMADO','VENCIDO','CANCELADO','ARCHIVADO'
);

CREATE TABLE IF NOT EXISTS documentocontrato (
    iddocumento        BIGSERIAL      PRIMARY KEY,
    idcontrato         INT            NOT NULL REFERENCES contrato(idcontrato) ON DELETE CASCADE,
    nombre             VARCHAR(300)   NOT NULL,
    archivourl         VARCHAR(500)   NOT NULL,
    tipoarchivo        VARCHAR(50)    NOT NULL,
    tamanobytes        BIGINT,
    idusuariocarga     INT            NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacarga         TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_doccontrato_contrato ON documentocontrato(idcontrato);

CREATE TABLE IF NOT EXISTS actividadcontrato (
    idactividad        BIGSERIAL      PRIMARY KEY,
    idcontrato         INT            NOT NULL REFERENCES contrato(idcontrato) ON DELETE CASCADE,
    accion             VARCHAR(80)    NOT NULL,
    descripcion        VARCHAR(400),
    idusuario          INT            REFERENCES usuarioadmin(idusuarioadmin),
    fechaaccion        TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_actividadcontrato_contrato ON actividadcontrato(idcontrato);