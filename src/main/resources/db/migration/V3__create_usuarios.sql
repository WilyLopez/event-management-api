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

CREATE TABLE preferenciaadmin (
    idpreferenciaadmin     BIGSERIAL    PRIMARY KEY,
    idusuarioadmin         BIGINT       NOT NULL REFERENCES usuarioadmin(idusuarioadmin) ON DELETE CASCADE,

    tema                   VARCHAR(20)  NOT NULL DEFAULT 'SYSTEM'
                               CHECK (tema IN ('LIGHT','DARK','SYSTEM')),
    colorprimario          VARCHAR(20),
    colorsecundario        VARCHAR(20),
    colorsidebar           VARCHAR(20),
    coloracento            VARCHAR(20),

    tipografia             VARCHAR(50)  NOT NULL DEFAULT 'INTER',
    tamanofuente           VARCHAR(20)  NOT NULL DEFAULT 'NORMAL'
                               CHECK (tamanofuente IN ('SMALL','NORMAL','LARGE')),
    radiosbordes           VARCHAR(20)  NOT NULL DEFAULT 'NORMAL'
                               CHECK (radiosbordes IN ('SMALL','NORMAL','LARGE')),

    sidebarcolapsado       BOOLEAN      NOT NULL DEFAULT FALSE,
    sidebarflotante        BOOLEAN      NOT NULL DEFAULT FALSE,
    modocompacto           BOOLEAN      NOT NULL DEFAULT FALSE,
    anchocontenido         VARCHAR(20)  NOT NULL DEFAULT 'FULL'
                               CHECK (anchocontenido IN ('BOXED','FULL')),
    mostrarmigaspan        BOOLEAN      NOT NULL DEFAULT TRUE,
    mostrariconosmenu      BOOLEAN      NOT NULL DEFAULT TRUE,

    mostraranimaciones     BOOLEAN      NOT NULL DEFAULT TRUE,
    animacionsidebar       BOOLEAN      NOT NULL DEFAULT TRUE,
    hovereffects           BOOLEAN      NOT NULL DEFAULT TRUE,
    loadersanimados        BOOLEAN      NOT NULL DEFAULT TRUE,

    confirmaracciones      BOOLEAN      NOT NULL DEFAULT TRUE,
    recordarultimapagina   BOOLEAN      NOT NULL DEFAULT TRUE,
    restaurartabs          BOOLEAN      NOT NULL DEFAULT FALSE,
    autorefreshdashboard   BOOLEAN      NOT NULL DEFAULT FALSE,
    intervalorefreshseg    INT                   DEFAULT 60,

    dashboardpersonalizado JSONB,
    widgetsvisibles        JSONB,
    accesosrapidos         JSONB,
    ordenwidgets           JSONB,
    layoutdashboard        JSONB,

    filtrospersistentes    JSONB,
    columnasvisibles       JSONB,
    ordenamientotablas     JSONB,
    elementosportabla      INT          NOT NULL DEFAULT 10,

    sonidonotificaciones   BOOLEAN      NOT NULL DEFAULT TRUE,
    notificacionespush     BOOLEAN      NOT NULL DEFAULT TRUE,
    notificacionesemail    BOOLEAN      NOT NULL DEFAULT TRUE,
    notificacionesvisuales BOOLEAN      NOT NULL DEFAULT TRUE,
    badgesdinamicos        BOOLEAN      NOT NULL DEFAULT TRUE,

    idioma                 VARCHAR(10)  NOT NULL DEFAULT 'es',
    zonahoraria            VARCHAR(60)  NOT NULL DEFAULT 'America/Lima',
    formatofecha           VARCHAR(30)  NOT NULL DEFAULT 'DD/MM/YYYY',
    formatohora            VARCHAR(20)  NOT NULL DEFAULT '24H',
    primerdiasemana        VARCHAR(20)  NOT NULL DEFAULT 'MONDAY'
                               CHECK (primerdiasemana IN ('SUNDAY','MONDAY')),

    altocontraste          BOOLEAN      NOT NULL DEFAULT FALSE,
    reduciranimaciones     BOOLEAN      NOT NULL DEFAULT FALSE,
    aumentarespaciado      BOOLEAN      NOT NULL DEFAULT FALSE,
    cursorgrande           BOOLEAN      NOT NULL DEFAULT FALSE,

    fechacreacion          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    UNIQUE (idusuarioadmin)
);

CREATE INDEX idx_preferenciaadmin_usuario ON preferenciaadmin(idusuarioadmin);
CREATE INDEX idx_preferenciaadmin_tema    ON preferenciaadmin(tema);
CREATE INDEX idx_preferenciaadmin_idioma  ON preferenciaadmin(idioma);
