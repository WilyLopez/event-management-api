-- V5: Eventos, reservas y contratos

CREATE TABLE reservapublica (
    idreservapublica   BIGSERIAL        PRIMARY KEY,
    idcliente          INT           NOT NULL REFERENCES cliente(idcliente),
    idsede             INT           NOT NULL REFERENCES sede(idsede),
    idestado           VARCHAR(40)   NOT NULL REFERENCES estadoreservapublica(codigo),
    idcanalreserva     VARCHAR(30)   NOT NULL REFERENCES canalreserva(idcanalreserva),
    idtipodiacod       VARCHAR(30)   NOT NULL REFERENCES tipodia(idtipodiacod),
    idreservaoriginal  INT           REFERENCES reservapublica(idreservapublica),
    esreprogramacion   BOOLEAN       NOT NULL DEFAULT FALSE,
    vecesreprogramada  INT      NOT NULL DEFAULT 0 CHECK (vecesreprogramada >= 0),
    fechaevento        DATE          NOT NULL,
    numeroticket       VARCHAR(50)   NOT NULL UNIQUE,
    preciohistorico    NUMERIC(10,2) NOT NULL CHECK (preciohistorico >= 0),
    descuentoaplicado  NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (descuentoaplicado >= 0),
    totalpagado        NUMERIC(10,2) NOT NULL CHECK (totalpagado >= 0),
    nombrenino         VARCHAR(120)  NOT NULL,
    edadnino           INT      NOT NULL CHECK (edadnino BETWEEN 0 AND 17),
    nombreacompanante  VARCHAR(120)  NOT NULL,
    dniacompanante     VARCHAR(8)    NOT NULL,
    firmoconsentimiento BOOLEAN      NOT NULL DEFAULT FALSE,
    motivocancelacion  VARCHAR(300),
    fechacreacion      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_reprog_tiene_original CHECK (esreprogramacion = FALSE OR idreservaoriginal IS NOT NULL),
    CONSTRAINT ck_total_coherente       CHECK (totalpagado = preciohistorico - descuentoaplicado)
);

CREATE INDEX idx_reservapub_cliente    ON reservapublica(idcliente);
CREATE INDEX idx_reservapub_fecha      ON reservapublica(fechaevento);
CREATE INDEX idx_reservapub_estado     ON reservapublica(idestado);
CREATE INDEX idx_reservapub_ticket     ON reservapublica(numeroticket);
CREATE INDEX idx_reservapub_sede_fecha ON reservapublica(idsede, fechaevento);
CREATE INDEX idx_reservapub_original   ON reservapublica(idreservaoriginal) WHERE idreservaoriginal IS NOT NULL;

CREATE TABLE eventoprivado (
    ideventoprivado     BIGSERIAL        PRIMARY KEY,
    idcliente           INT           NOT NULL REFERENCES cliente(idcliente),
    idsede              INT           NOT NULL REFERENCES sede(idsede),
    idestado            VARCHAR(40)   NOT NULL REFERENCES estadoeventoprivado(codigo),
    idturno             INT           NOT NULL REFERENCES turno(idturno),
    fechaevento         DATE          NOT NULL,
    tipoevento          VARCHAR(200)  NOT NULL,
    contactoadicional   VARCHAR(200),
    aforodeclarado      INT      CHECK (aforodeclarado BETWEEN 1 AND 60),
    preciototalcontrato NUMERIC(10,2) CHECK (preciototalcontrato >= 0),
    montoadelanto       NUMERIC(10,2) DEFAULT 0 CHECK (montoadelanto >= 0),
    motivocancelacion   VARCHAR(500),
    notasinternas       TEXT,
    idusuariogestor     INT           REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    fechaactualizacion  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_evento_sede_fecha_turno UNIQUE (idsede, fechaevento, idturno)
);

CREATE INDEX idx_eventopri_cliente    ON eventoprivado(idcliente);
CREATE INDEX idx_eventopri_sede_fecha ON eventoprivado(idsede, fechaevento);
CREATE INDEX idx_eventopri_estado     ON eventoprivado(idestado);

CREATE TABLE proveedor (
    idproveedor        BIGSERIAL       PRIMARY KEY,
    nombre             VARCHAR(200) NOT NULL,
    ruc                VARCHAR(11)  UNIQUE,
    contactonombre     VARCHAR(120),
    contactotelefono   VARCHAR(20),
    contactocorreo     VARCHAR(120),
    tiposervicio       VARCHAR(200) NOT NULL,
    notas              TEXT,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE contrato (
    idcontrato         BIGSERIAL      PRIMARY KEY,
    ideventoprivado    INT         NOT NULL UNIQUE REFERENCES eventoprivado(ideventoprivado),
    idestado           VARCHAR(40) NOT NULL REFERENCES estadocontrato(codigo),
    contenidotexto     TEXT        NOT NULL,
    archivopdfurl      VARCHAR(500),
    fechafirma         DATE,
    idusuarioredactor  INT         NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE contratoproveedor (
    idcontratoproveedor BIGSERIAL        PRIMARY KEY,
    idcontrato          INT           NOT NULL REFERENCES contrato(idcontrato),
    idproveedor         INT           NOT NULL REFERENCES proveedor(idproveedor),
    serviciodescripcion VARCHAR(300),
    montoacordado       NUMERIC(10,2) CHECK (montoacordado >= 0),
    contratadopor       VARCHAR(30)   NOT NULL DEFAULT 'EMPRESA' CHECK (contratadopor IN ('EMPRESA','CLIENTE')),
    UNIQUE (idcontrato, idproveedor)
);