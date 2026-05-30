ALTER TABLE eventoprivado
    ADD COLUMN IF NOT EXISTS nombre_nino          VARCHAR(120),
    ADD COLUMN IF NOT EXISTS edad_cumple          INT,
    ADD COLUMN IF NOT EXISTS medio_pago_adelanto  VARCHAR(30),
    ADD COLUMN IF NOT EXISTS observaciones        TEXT,
    ADD COLUMN IF NOT EXISTS idpaquete            BIGINT REFERENCES paqueteevento(idpaquete);

CREATE TABLE IF NOT EXISTS extra_paquete (
    idextra       BIGSERIAL    PRIMARY KEY,
    idpaquete     BIGINT       NOT NULL REFERENCES paqueteevento(idpaquete),
    nombre        VARCHAR(150) NOT NULL,
    descripcion   VARCHAR(300),
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    orden         INT          NOT NULL DEFAULT 0,
    fechacreacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_extrapaquete_paquete ON extra_paquete (idpaquete);

CREATE TABLE IF NOT EXISTS evento_extra (
    ideventoextra   BIGSERIAL PRIMARY KEY,
    ideventoprivado BIGINT    NOT NULL REFERENCES eventoprivado(ideventoprivado),
    idextra         BIGINT    REFERENCES extra_paquete(idextra),
    nombrelibre     VARCHAR(300),
    fechacreacion   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_eventoextra_evento ON evento_extra (ideventoprivado);
