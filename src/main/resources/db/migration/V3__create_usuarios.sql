-- V3: Usuarios administrativos, clientes y sesiones

-- ─── Administradores del sistema ─────────────────────────────────────────────
CREATE TABLE usuarioadmin (
    idusuarioadmin         BIGSERIAL    PRIMARY KEY,
    idsede                 BIGINT       NOT NULL REFERENCES sede(idsede),
    nombre                 VARCHAR(120) NOT NULL,
    correo                 VARCHAR(120) NOT NULL UNIQUE,
    contresenahash         VARCHAR(255) NOT NULL,
    rol                    VARCHAR(30)  NOT NULL DEFAULT 'ADMINISTRATIVO',
    fotoperfilurl          VARCHAR(500),
    telefono               VARCHAR(20),
    activo                 BOOLEAN      NOT NULL DEFAULT TRUE,
    intentosfallidos       INT          NOT NULL DEFAULT 0,
    bloqueadohasta         TIMESTAMPTZ,
    ultimoacceso           TIMESTAMPTZ,
    debecambiarcontrasena  BOOLEAN      NOT NULL DEFAULT TRUE,
    ultimocambiocontrasena TIMESTAMPTZ,
    creado_por             BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuarioadmin_correo ON usuarioadmin(correo);
CREATE INDEX idx_usuarioadmin_sede   ON usuarioadmin(idsede);

-- ─── Clientes del negocio ─────────────────────────────────────────────────────
CREATE TABLE cliente (
    idcliente          BIGSERIAL    PRIMARY KEY,
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
    contadorvisitas    INT          NOT NULL DEFAULT 0 CHECK (contadorvisitas >= 0),
    correoverificado   BOOLEAN      NOT NULL DEFAULT FALSE,
    tokenverificacion  VARCHAR(255),
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    fotoperfil         VARCHAR(500),
    ultimologin        TIMESTAMPTZ,
    fechanacimiento    DATE,
    tipocliente        VARCHAR(20)  NOT NULL DEFAULT 'PERSONA'
                           CHECK (tipocliente IN ('PERSONA', 'EMPRESA')),
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_ruc_requiere_razonsocial CHECK (ruc IS NULL OR razonsocial IS NOT NULL)
);

CREATE INDEX idx_cliente_correo      ON cliente(correo);
CREATE INDEX idx_cliente_dni         ON cliente(dni);
CREATE INDEX idx_cliente_activo      ON cliente(activo);
CREATE INDEX idx_cliente_tipocliente ON cliente(tipocliente);
CREATE INDEX idx_cliente_ultimologin ON cliente(ultimologin DESC NULLS LAST);

-- ─── Sesiones administrativas (JWT tracking) ──────────────────────────────────
CREATE TABLE sesionadmin (
    idsesion        BIGSERIAL    PRIMARY KEY,
    idusuarioadmin  BIGINT       NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    ip              INET,
    useragent       VARCHAR(300),
    tokenjti        VARCHAR(120),
    iniciologin     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    ultimaactividad TIMESTAMPTZ,
    cerrada         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_sesionadmin_usuario ON sesionadmin(idusuarioadmin);
CREATE INDEX idx_sesionadmin_activa  ON sesionadmin(idusuarioadmin, cerrada);
CREATE INDEX idx_sesionadmin_token   ON sesionadmin(tokenjti) WHERE tokenjti IS NOT NULL;
