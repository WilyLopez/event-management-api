CREATE TABLE tipoegreso (
    idtipoegreso       BIGSERIAL     PRIMARY KEY,
    nombre             VARCHAR(120)  NOT NULL,
    descripcion        VARCHAR(300),
    categoria          VARCHAR(30)   NOT NULL
                         CHECK (categoria IN ('RECURRENTE_FIJO','RECURRENTE_VARIABLE','EVENTUAL')),
    activo             BOOLEAN       NOT NULL DEFAULT TRUE,
    idusuariocreador   BIGINT        REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

INSERT INTO tipoegreso (nombre, descripcion, categoria) VALUES
    ('Electricidad',  'Pago del servicio de electricidad',    'RECURRENTE_VARIABLE'),
    ('Agua',          'Pago del servicio de agua',            'RECURRENTE_VARIABLE'),
    ('Internet',      'Pago del servicio de internet',        'RECURRENTE_FIJO'),
    ('Alquiler local','Pago del alquiler del local',          'RECURRENTE_FIJO'),
    ('Sueldos',       'Pago de sueldos al personal',          'RECURRENTE_VARIABLE'),
    ('Limpieza',      'Insumos y servicios de limpieza',      'RECURRENTE_VARIABLE'),
    ('Reparaciones',  'Reparaciones y mantenimiento',         'EVENTUAL'),
    ('Equipamiento',  'Compra de equipos o materiales',       'EVENTUAL');

CREATE TABLE registroegreso (
    idregistroegreso   BIGSERIAL      PRIMARY KEY,
    idtipoegreso       BIGINT         NOT NULL REFERENCES tipoegreso(idtipoegreso),
    idsede             BIGINT         NOT NULL REFERENCES sede(idsede),
    monto              NUMERIC(10,2)  NOT NULL CHECK (monto > 0),
    fecha              DATE           NOT NULL,
    periodoaño         INT,
    periodomes         INT            CHECK (periodomes BETWEEN 1 AND 12),
    descripcion        VARCHAR(300),
    comprobanteurl     VARCHAR(500),
    esrecurrente       BOOLEAN        NOT NULL DEFAULT FALSE,
    idusuarioregistra  BIGINT         NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_regegreso_sede_fecha   ON registroegreso (idsede, fecha);
CREATE INDEX idx_regegreso_tipo         ON registroegreso (idtipoegreso);
CREATE INDEX idx_regegreso_periodo      ON registroegreso (periodoaño, periodomes);

CREATE TABLE gastoeventoprivado (
    idgasto            BIGSERIAL      PRIMARY KEY,
    ideventoprivado    BIGINT         NOT NULL REFERENCES eventoprivado(ideventoprivado),
    descripcion        VARCHAR(200)   NOT NULL,
    monto              NUMERIC(10,2)  NOT NULL CHECK (monto > 0),
    comprobanteurl     VARCHAR(500),
    idusuarioregistra  BIGINT         NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_gastoeventopri_evento ON gastoeventoprivado (ideventoprivado);

CREATE TABLE gastoopertaiviodiario (
    idgastooperativo   BIGSERIAL      PRIMARY KEY,
    idsede             BIGINT         NOT NULL REFERENCES sede(idsede),
    fecha              DATE           NOT NULL,
    descripcion        VARCHAR(200)   NOT NULL,
    monto              NUMERIC(10,2)  NOT NULL CHECK (monto > 0),
    comprobanteurl     VARCHAR(500),
    idusuarioregistra  BIGINT         NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_gastooperativo_sede_fecha ON gastoopertaiviodiario (idsede, fecha);
