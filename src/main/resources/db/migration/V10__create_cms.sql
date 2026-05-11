-- V10: CMS y contenido web público

-- ─── Contenido web por sección ────────────────────────────────────────────────
-- idseccion e idtipocontenido son claves gestionadas por la aplicación (sin FK).
CREATE TABLE contenidoweb (
    idcontenidoweb     BIGSERIAL    PRIMARY KEY,
    idseccion          INT          NOT NULL,
    idtipocontenido    INT          NOT NULL,
    clave              VARCHAR(100) NOT NULL,
    valores            TEXT         NOT NULL,
    valoren            TEXT,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioeditor    BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (idseccion, clave)
);

CREATE INDEX idx_contenido_seccion ON contenidoweb(idseccion);

-- ─── Galería de imágenes del local ────────────────────────────────────────────
CREATE TABLE imagengaleria (
    idimagengaleria    BIGSERIAL    PRIMARY KEY,
    idsede             BIGINT       NOT NULL REFERENCES sede(idsede),
    urlimagen          VARCHAR(500) NOT NULL,
    alttexto           VARCHAR(200),
    categoriaimagen    VARCHAR(20)  NOT NULL
                           CHECK (categoriaimagen IN ('CUMPLEANOS', 'JUEGOS', 'DECORACION', 'GENERAL', 'EVENTO')),
    ordenvisualizacion INT          NOT NULL DEFAULT 0,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuariosubio     BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechasubida        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_imagen_sede_cat ON imagengaleria(idsede, categoriaimagen);
CREATE INDEX idx_imagen_orden    ON imagengaleria(ordenvisualizacion);

-- ─── Banners promocionales del sitio web ──────────────────────────────────────
CREATE TABLE banner (
    idbanner         BIGSERIAL    PRIMARY KEY,
    idsede           BIGINT       REFERENCES sede(idsede),
    titulo           VARCHAR(200) NOT NULL,
    descripcion      VARCHAR(400),
    imagenurl        VARCHAR(500) NOT NULL,
    enlacedestino    VARCHAR(500),
    fechainicio      DATE         NOT NULL,
    fechafin         DATE,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    orden            INT          NOT NULL DEFAULT 0,
    idusuariocreador BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_banner_fechas CHECK (fechafin IS NULL OR fechafin >= fechainicio)
);

CREATE INDEX idx_banner_fechas ON banner(fechainicio, fechafin);
CREATE INDEX idx_banner_activo ON banner(activo);

-- ─── Reseñas de clientes ──────────────────────────────────────────────────────
CREATE TABLE resena (
    idresena         BIGSERIAL    PRIMARY KEY,
    idcliente        BIGINT       REFERENCES cliente(idcliente),
    nombreautor      VARCHAR(120) NOT NULL,
    contenido        TEXT         NOT NULL,
    calificacion     INT          NOT NULL CHECK (calificacion BETWEEN 1 AND 5),
    aprobada         BOOLEAN      NOT NULL DEFAULT FALSE,
    idusuarioaprueba BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_resena_aprobada ON resena(aprobada);
