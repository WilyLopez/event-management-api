-- V4: Feriados, bloqueos de calendario, disponibilidad diaria y tarifas

-- ─── Feriados nacionales y regionales ────────────────────────────────────────
CREATE TABLE feriado (
    idferiado     BIGSERIAL    PRIMARY KEY,
    idtipoferiado VARCHAR(20)  NOT NULL REFERENCES tipoferiado(idtipoferiado),
    fecha         DATE         NOT NULL UNIQUE,
    descripcion   VARCHAR(120) NOT NULL,
    anio          INT          NOT NULL GENERATED ALWAYS AS (EXTRACT(YEAR FROM fecha)::INT) STORED,
    creadopor     BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_feriado_fecha ON feriado(fecha);
CREATE INDEX idx_feriado_anio  ON feriado(anio);

-- ─── Bloqueos manuales del calendario ────────────────────────────────────────
CREATE TABLE bloquecalendario (
    idbloquecalendario BIGSERIAL    PRIMARY KEY,
    idsede             BIGINT       NOT NULL REFERENCES sede(idsede),
    fechainicio        DATE         NOT NULL,
    fechafin           DATE         NOT NULL,
    motivo             VARCHAR(300) NOT NULL,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuariocreador   BIGINT       NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_bloque_fechas CHECK (fechafin >= fechainicio)
);

CREATE INDEX idx_bloque_sede_fechas ON bloquecalendario(idsede, fechainicio, fechafin);

-- ─── Disponibilidad diaria (aforo y turnos) ───────────────────────────────────
CREATE TABLE disponibilidaddiaria (
    iddisponibilidad    BIGSERIAL   PRIMARY KEY,
    idsede              BIGINT      NOT NULL REFERENCES sede(idsede),
    fecha               DATE        NOT NULL,
    accesopublicoactivo BOOLEAN     NOT NULL DEFAULT TRUE,
    turnot1disponible   BOOLEAN     NOT NULL DEFAULT TRUE,
    turnot2disponible   BOOLEAN     NOT NULL DEFAULT TRUE,
    aforopublicoactual  INT         NOT NULL DEFAULT 0 CHECK (aforopublicoactual >= 0),
    fechaactualizacion  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (idsede, fecha)
);

CREATE INDEX idx_disp_sede_fecha ON disponibilidaddiaria(idsede, fecha);

-- ─── Tarifas por tipo de día ──────────────────────────────────────────────────
CREATE TABLE tarifa (
    idtarifa         BIGSERIAL     PRIMARY KEY,
    idsede           BIGINT        NOT NULL REFERENCES sede(idsede),
    idtipodiacod     VARCHAR(30)   NOT NULL REFERENCES tipodia(idtipodiacod),
    precio           NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    vigenciadesde    DATE          NOT NULL,
    vigenciahasta    DATE,
    activo           BOOLEAN       NOT NULL DEFAULT TRUE,
    idusuariocreador BIGINT        NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_tarifa_vigencia CHECK (vigenciahasta IS NULL OR vigenciahasta >= vigenciadesde),
    UNIQUE (idsede, idtipodiacod, vigenciadesde)
);

CREATE INDEX idx_tarifa_sede_dia ON tarifa(idsede, idtipodiacod);
CREATE INDEX idx_tarifa_vigencia ON tarifa(vigenciadesde, vigenciahasta);
