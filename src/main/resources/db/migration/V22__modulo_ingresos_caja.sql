CREATE TABLE tipoingreso (
    idtipoingreso     BIGSERIAL     PRIMARY KEY,
    nombre            VARCHAR(120)  NOT NULL,
    descripcion       VARCHAR(300),
    categoria         VARCHAR(30)   NOT NULL
                        CHECK (categoria IN ('RESERVA_PUBLICA','ADELANTO_EVENTO','INGRESO_MANUAL','OTRO')),
    activo            BOOLEAN       NOT NULL DEFAULT TRUE,
    fechacreacion     TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

INSERT INTO tipoingreso (nombre, descripcion, categoria) VALUES
    ('Reserva pública',         'Ingreso por reservas de entradas al parque',  'RESERVA_PUBLICA'),
    ('Adelanto evento privado', 'Adelanto por contrato de evento privado',      'ADELANTO_EVENTO'),
    ('Ingreso manual',          'Ingreso registrado manualmente por operario',  'INGRESO_MANUAL'),
    ('Otro ingreso',            'Ingreso de categoría no definida',             'OTRO');

CREATE TABLE registroingreso (
    idregistroingreso   BIGSERIAL      PRIMARY KEY,
    idtipoingreso       BIGINT         NOT NULL REFERENCES tipoingreso(idtipoingreso),
    idsede              BIGINT         NOT NULL REFERENCES sede(idsede),
    idreservapublica    BIGINT         REFERENCES reservapublica(idreservapublica),
    ideventoprivado     BIGINT         REFERENCES eventoprivado(ideventoprivado),
    monto               NUMERIC(10,2)  NOT NULL CHECK (monto > 0),
    fecha               DATE           NOT NULL,
    mediopago           VARCHAR(30),
    descripcion         VARCHAR(300),
    esautomatico        BOOLEAN        NOT NULL DEFAULT FALSE,
    idusuarioregistra   BIGINT         REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion       TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_regingreso_sede_fecha     ON registroingreso (idsede, fecha);
CREATE INDEX idx_regingreso_tipo           ON registroingreso (idtipoingreso);
CREATE INDEX idx_regingreso_reservapublica ON registroingreso (idreservapublica)
    WHERE idreservapublica IS NOT NULL;
CREATE INDEX idx_regingreso_eventoprivado  ON registroingreso (ideventoprivado)
    WHERE ideventoprivado IS NOT NULL;

CREATE TABLE aperturacaja (
    idaperturacaja      BIGSERIAL      PRIMARY KEY,
    idsede              BIGINT         NOT NULL REFERENCES sede(idsede),
    fecha               DATE           NOT NULL,
    saldoinicial        NUMERIC(10,2)  NOT NULL DEFAULT 0,
    saldofinal          NUMERIC(10,2),
    totalingresos       NUMERIC(10,2)  NOT NULL DEFAULT 0,
    totalegresos        NUMERIC(10,2)  NOT NULL DEFAULT 0,
    estado              VARCHAR(20)    NOT NULL DEFAULT 'ABIERTA'
                          CHECK (estado IN ('ABIERTA','CERRADA')),
    idusuarioapertura   BIGINT         REFERENCES usuarioadmin(idusuarioadmin),
    idusuariocierre     BIGINT         REFERENCES usuarioadmin(idusuarioadmin),
    fechaapertura       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fechacierre         TIMESTAMPTZ,
    observaciones       VARCHAR(500),
    fechacreacion       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (idsede, fecha)
);

CREATE INDEX idx_aperturacaja_sede_fecha ON aperturacaja (idsede, fecha);

CREATE TABLE movimientocaja (
    idmovimientocaja    BIGSERIAL      PRIMARY KEY,
    idaperturacaja      BIGINT         NOT NULL REFERENCES aperturacaja(idaperturacaja),
    tipo                VARCHAR(20)    NOT NULL CHECK (tipo IN ('INGRESO','EGRESO')),
    concepto            VARCHAR(200)   NOT NULL,
    monto               NUMERIC(10,2)  NOT NULL CHECK (monto > 0),
    mediopago           VARCHAR(30),
    idregistroingreso   BIGINT         REFERENCES registroingreso(idregistroingreso),
    idregistroegreso    BIGINT         REFERENCES registroegreso(idregistroegreso),
    idreservapublica    BIGINT         REFERENCES reservapublica(idreservapublica),
    esmanual            BOOLEAN        NOT NULL DEFAULT FALSE,
    idusuarioregistra   BIGINT         REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion       TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimientocaja_apertura ON movimientocaja (idaperturacaja);

CREATE TABLE presupuestoevento (
    idpresupuesto       BIGSERIAL      PRIMARY KEY,
    ideventoprivado     BIGINT         NOT NULL REFERENCES eventoprivado(ideventoprivado),
    concepto            VARCHAR(200)   NOT NULL,
    categoria           VARCHAR(50),
    montoestimado       NUMERIC(10,2)  NOT NULL CHECK (montoestimado >= 0),
    montoreal           NUMERIC(10,2)  CHECK (montoreal >= 0),
    estado              VARCHAR(20)    NOT NULL DEFAULT 'PENDIENTE'
                          CHECK (estado IN ('PENDIENTE','APROBADO','EJECUTADO')),
    idusuarioregistra   BIGINT         REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fechaactualizacion  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_presupuestoevento_evento ON presupuestoevento (ideventoprivado);

INSERT INTO registroingreso
    (idtipoingreso, idsede, idreservapublica, monto, fecha, mediopago, descripcion, esautomatico)
SELECT
    (SELECT idtipoingreso FROM tipoingreso WHERE categoria = 'RESERVA_PUBLICA' LIMIT 1),
    r.idsede,
    r.idreservapublica,
    r.totalpagado,
    r.fechaevento,
    r.mediopago,
    'Migración histórica — reserva ' || r.numeroticket,
    TRUE
FROM reservapublica r
WHERE r.idestado IN ('CONFIRMADA', 'COMPLETADA', 'REPROGRAMADA')
  AND r.totalpagado > 0;

INSERT INTO registroingreso
    (idtipoingreso, idsede, ideventoprivado, monto, fecha, descripcion, esautomatico)
SELECT
    (SELECT idtipoingreso FROM tipoingreso WHERE categoria = 'ADELANTO_EVENTO' LIMIT 1),
    e.idsede,
    e.ideventoprivado,
    e.montoadelanto,
    e.fechaevento,
    'Migración histórica — adelanto evento ' || e.ideventoprivado,
    TRUE
FROM eventoprivado e
WHERE e.idestado IN ('CONFIRMADA', 'COMPLETADA')
  AND e.montoadelanto > 0;
