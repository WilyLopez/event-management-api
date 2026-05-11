ALTER TABLE reservapublica
    ADD COLUMN IF NOT EXISTS ingresado      BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS fechaingreso   TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS codigoqr       VARCHAR(200),
    ADD COLUMN IF NOT EXISTS mediopago      VARCHAR(30),
    ADD COLUMN IF NOT EXISTS referenciapago VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_reservapub_ingresado
    ON reservapublica (ingresado) WHERE ingresado = TRUE;

ALTER TABLE eventoprivado
    ADD COLUMN IF NOT EXISTS estadooperativo    VARCHAR(40),
    ADD COLUMN IF NOT EXISTS checklistcompleto  BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS horainicioreal     TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS horafinreal        TIMESTAMPTZ;

CREATE TABLE IF NOT EXISTS checklistevento (
    idchecklist        BIGSERIAL     PRIMARY KEY,
    ideventoprivado    BIGINT        NOT NULL REFERENCES eventoprivado(ideventoprivado) ON DELETE CASCADE,
    tarea              VARCHAR(200)  NOT NULL,
    completada         BOOLEAN       NOT NULL DEFAULT FALSE,
    orden              INT           NOT NULL DEFAULT 0,
    idusuariocompleto  BIGINT        REFERENCES usuarioadmin(idusuarioadmin),
    fechacompletado    TIMESTAMPTZ,
    fechacreacion      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_checklist_evento ON checklistevento (ideventoprivado);