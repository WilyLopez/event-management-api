-- V3: Tablas de usuarios y acceso

CREATE TABLE usuarioadmin (
    idusuarioadmin     BIGSERIAL       PRIMARY KEY,
    idsede             INT          NOT NULL REFERENCES sede(idsede),
    nombre             VARCHAR(120) NOT NULL,
    correo             VARCHAR(120) NOT NULL UNIQUE,
    contresenahash     VARCHAR(255) NOT NULL,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    intentosfallidos   INT     NOT NULL DEFAULT 0,
    bloqueadohasta     TIMESTAMPTZ,
    ultimoacceso       TIMESTAMPTZ,
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuarioadmin_correo ON usuarioadmin(correo);
CREATE INDEX idx_usuarioadmin_sede   ON usuarioadmin(idsede);

CREATE TABLE cliente (
    idcliente          BIGSERIAL       PRIMARY KEY,
    nombre             VARCHAR(120) NOT NULL,
    correo             VARCHAR(120) NOT NULL UNIQUE,
    contresenahash     VARCHAR(255) NOT NULL,
    telefono           VARCHAR(20)  NOT NULL,
    dni                VARCHAR(8),
    ruc                VARCHAR(11),
    razonsocial        VARCHAR(200),
    direccionfiscal    VARCHAR(300),
    esvip              BOOLEAN      NOT NULL DEFAULT FALSE,
    descuentovip       NUMERIC(5,2) CHECK (descuentovip BETWEEN 0 AND 100),
    contadorvisitas    INT     NOT NULL DEFAULT 0 CHECK (contadorvisitas >= 0),
    correoverificado   BOOLEAN      NOT NULL DEFAULT FALSE,
    tokenverificacion  VARCHAR(255),
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_ruc_requiere_razonsocial CHECK (ruc IS NULL OR razonsocial IS NOT NULL)
);

CREATE INDEX idx_cliente_correo ON cliente(correo);
CREATE INDEX idx_cliente_dni    ON cliente(dni);
CREATE INDEX idx_cliente_activo ON cliente(activo);