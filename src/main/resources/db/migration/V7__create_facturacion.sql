-- V7: Facturación electrónica SUNAT

CREATE TABLE seriecomprobante (
    idserie            BIGSERIAL      PRIMARY KEY,
    idsede             INT         NOT NULL REFERENCES sede(idsede),
    idtipo             VARCHAR(30) NOT NULL REFERENCES tipocomprobante(codigo),
    serie              VARCHAR(4)  NOT NULL,
    correlativoactual  INT         NOT NULL DEFAULT 0,
    activo             BOOLEAN     NOT NULL DEFAULT TRUE,
    UNIQUE (idsede, serie)
);

CREATE TABLE comprobante (
    idcomprobante       BIGSERIAL        PRIMARY KEY,
    idpago              INT           NOT NULL UNIQUE REFERENCES pago(idpago),
    idtipo              VARCHAR(30)   NOT NULL REFERENCES tipocomprobante(codigo),
    idestado            VARCHAR(40)   NOT NULL REFERENCES estadocomprobante(codigo),
    idserie             INT           NOT NULL REFERENCES seriecomprobante(idserie),
    serienum            VARCHAR(4)    NOT NULL,
    correlativo         VARCHAR(8)    NOT NULL,
    numerocompleto      VARCHAR(20)   NOT NULL UNIQUE,
    rucemisor           VARCHAR(11)   NOT NULL,
    razonsocialemisor   VARCHAR(200)  NOT NULL,
    tipodocreceptor     VARCHAR(20)   NOT NULL CHECK (tipodocreceptor IN ('DNI','RUC','CE','PASAPORTE','SIN_DOC')),
    nrodocreceptor      VARCHAR(20),
    razonsocialreceptor VARCHAR(200),
    direccionreceptor   VARCHAR(300),
    montobase           NUMERIC(10,2) NOT NULL CHECK (montobase >= 0),
    montoigv            NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (montoigv >= 0),
    montototal          NUMERIC(10,2) NOT NULL CHECK (montototal >= 0),
    xmlurl              VARCHAR(500),
    pdfurl              VARCHAR(500),
    hashsunat           VARCHAR(200),
    cdrestado           VARCHAR(50),
    cdrdescripcion      VARCHAR(500),
    motivoanulacion     VARCHAR(300),
    idcomprobantenta    INT           REFERENCES comprobante(idcomprobante),
    fechaemision        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    fechaactualizacion  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comp_pago      ON comprobante(idpago);
CREATE INDEX idx_comp_serie_cor ON comprobante(serienum, correlativo);
CREATE INDEX idx_comp_estado    ON comprobante(idestado);
CREATE INDEX idx_comp_fecha     ON comprobante(fechaemision);