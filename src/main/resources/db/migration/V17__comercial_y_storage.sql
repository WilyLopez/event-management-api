CREATE TABLE paqueteevento (
    idpaquete          BIGSERIAL      PRIMARY KEY,
    nombre             VARCHAR(30)    NOT NULL,
    slug               VARCHAR(40)    NOT NULL UNIQUE,
    descripcioncorta   VARCHAR(80)    NOT NULL,
    descripcionlarga   VARCHAR(500),
    precio             NUMERIC(10,2)  NOT NULL CHECK (precio > 0),
    badge              VARCHAR(20),
    color              VARCHAR(7),
    imagenurl          VARCHAR(500),
    duracionminutos    INT            CHECK (duracionminutos > 0),
    limitepersonas     INT            CHECK (limitepersonas > 0),
    activo             BOOLEAN        NOT NULL DEFAULT TRUE,
    destacado          BOOLEAN        NOT NULL DEFAULT FALSE,
    orden              INT            NOT NULL DEFAULT 0,
    fechacreacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE beneficiopaquete (
    idbeneficio        BIGSERIAL      PRIMARY KEY,
    idpaquete          BIGINT         NOT NULL REFERENCES paqueteevento(idpaquete) ON DELETE CASCADE,
    descripcion        VARCHAR(60)    NOT NULL,
    orden              INT            NOT NULL DEFAULT 0
);

CREATE TABLE zonajuego (
    idzona             BIGSERIAL      PRIMARY KEY,
    nombre             VARCHAR(25)    NOT NULL,
    slug               VARCHAR(35)    NOT NULL UNIQUE,
    descripcion        VARCHAR(100)   NOT NULL,
    edadminima         INT            CHECK (edadminima >= 0),
    edadmaxima         INT            CHECK (edadmaxima <= 17),
    activa             BOOLEAN        NOT NULL DEFAULT TRUE,
    destacada          BOOLEAN        NOT NULL DEFAULT FALSE,
    orden              INT            NOT NULL DEFAULT 0,
    fechacreacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE mediozonasjuego (
    idmedio            BIGSERIAL      PRIMARY KEY,
    idzona             BIGINT         NOT NULL REFERENCES zonajuego(idzona) ON DELETE CASCADE,
    tipo               VARCHAR(10)    NOT NULL CHECK (tipo IN ('IMAGEN','VIDEO')),
    url                VARCHAR(500)   NOT NULL,
    orden              INT            NOT NULL DEFAULT 0
);

CREATE TABLE actividadlocal (
    idactividad        BIGSERIAL      PRIMARY KEY,
    nombre             VARCHAR(40)    NOT NULL,
    descripcion        VARCHAR(100)   NOT NULL,
    imagenurl          VARCHAR(500),
    idzona             BIGINT         REFERENCES zonajuego(idzona),
    esespecial         BOOLEAN        NOT NULL DEFAULT FALSE,
    fechainicio        DATE,
    fechafin           DATE,
    activa             BOOLEAN        NOT NULL DEFAULT TRUE,
    destacada          BOOLEAN        NOT NULL DEFAULT FALSE,
    orden              INT            NOT NULL DEFAULT 0,
    fechacreacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE novedadlocal (
    idnovedad          BIGSERIAL      PRIMARY KEY,
    titulo             VARCHAR(50)    NOT NULL,
    descripcion        VARCHAR(120)   NOT NULL,
    imagenurl          VARCHAR(500),
    textocta           VARCHAR(25),
    urlcta             VARCHAR(300),
    prioridad          INT            NOT NULL DEFAULT 0,
    fechainicio        DATE,
    fechafin           DATE,
    visiblehome        BOOLEAN        NOT NULL DEFAULT FALSE,
    destacada          BOOLEAN        NOT NULL DEFAULT FALSE,
    activa             BOOLEAN        NOT NULL DEFAULT TRUE,
    fechacreacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_paquete_activo   ON paqueteevento (activo, orden);
CREATE INDEX idx_zona_activa      ON zonajuego (activa, orden);
CREATE INDEX idx_actividad_activa ON actividadlocal (activa, esespecial);
CREATE INDEX idx_novedad_home     ON novedadlocal (visiblehome, activa, prioridad);
