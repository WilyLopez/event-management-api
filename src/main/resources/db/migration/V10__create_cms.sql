CREATE TABLE seccionweb (
    idseccion          BIGSERIAL    PRIMARY KEY,
    codigo             VARCHAR(50)  NOT NULL UNIQUE,
    nombre             VARCHAR(120) NOT NULL,
    descripcion        VARCHAR(300),
    ordenvisualizacion INT          NOT NULL DEFAULT 0,
    visible            BOOLEAN      NOT NULL DEFAULT TRUE
);

INSERT INTO seccionweb (codigo, nombre, descripcion) VALUES
    ('HOME',        'Página principal',     'Contenido principal del home'),
    ('HEADER',      'Cabecera',             'Navbar público'),
    ('FOOTER',      'Pie de página',        'Footer del sitio'),
    ('CONTACTO',    'Contacto',             'Información de contacto'),
    ('NOSOTROS',    'Nosotros',             'Contenido institucional'),
    ('PROMOCIONES', 'Promociones',          'Promociones públicas'),
    ('GALERIA',     'Galería',              'Galería pública'),
    ('FAQ',         'Preguntas frecuentes', 'FAQs del sitio'),
    ('LEGAL',       'Contenido legal',      'Políticas y términos')
ON CONFLICT DO NOTHING;

CREATE TABLE tipocontenido (
    idtipocontenido BIGSERIAL    PRIMARY KEY,
    codigo          VARCHAR(40)  NOT NULL UNIQUE,
    descripcion     VARCHAR(120) NOT NULL
);

INSERT INTO tipocontenido (codigo, descripcion) VALUES
    ('TEXTO',    'Texto simple'),
    ('HTML',     'Contenido HTML'),
    ('URL',      'Enlace URL'),
    ('JSON',     'Contenido JSON'),
    ('EMAIL',    'Correo electrónico'),
    ('TELEFONO', 'Número telefónico'),
    ('COLOR',    'Color hexadecimal'),
    ('IMAGEN',   'Imagen'),
    ('BOOLEANO', 'Valor booleano')
ON CONFLICT DO NOTHING;

CREATE TABLE contenidoweb (
    idcontenidoweb     BIGSERIAL    PRIMARY KEY,
    idseccion          INT          NOT NULL,
    idtipocontenido    INT          NOT NULL,
    clave              VARCHAR(100) NOT NULL,
    valores            TEXT         NOT NULL,
    valoren            TEXT,
    imagenurl          VARCHAR(500),
    descripcion        VARCHAR(300),
    ordenvisualizacion INT          NOT NULL DEFAULT 0,
    visible            BOOLEAN      NOT NULL DEFAULT TRUE,
    version            INT          NOT NULL DEFAULT 1,
    metadatos          JSONB,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioeditor    BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (idseccion, clave)
);

CREATE INDEX idx_contenido_seccion         ON contenidoweb(idseccion);
CREATE INDEX idx_contenido_clave           ON contenidoweb(clave);
CREATE INDEX idx_contenido_visible         ON contenidoweb(visible);
CREATE INDEX idx_contenido_seccion_visible ON contenidoweb(idseccion, visible);

CREATE TABLE configuracionpublica (
    idconfiguracionpublica BIGSERIAL    PRIMARY KEY,
    nombrenegocio          VARCHAR(150) NOT NULL,
    slogan                 VARCHAR(250),
    logourl                VARCHAR(500),
    faviconurl             VARCHAR(500),
    telefono               VARCHAR(20),
    telefonosecundario     VARCHAR(20),
    whatsapp               VARCHAR(20),
    correo                 VARCHAR(120),
    correosecundario       VARCHAR(120),
    direccion              VARCHAR(300),
    facebookurl            VARCHAR(300),
    instagramurl           VARCHAR(300),
    tiktokurl              VARCHAR(300),
    youtubeurl             VARCHAR(300),
    googlemapsurl          VARCHAR(500),
    horariosemana          VARCHAR(120),
    horariofindesemana     VARCHAR(120),
    copyrighttexto         VARCHAR(300),
    metatitle              VARCHAR(200),
    metadescription        VARCHAR(500),
    metakeywords           VARCHAR(500),
    opengraphtitle         VARCHAR(200),
    opengraphdescription   VARCHAR(500),
    opengraphimageurl      VARCHAR(500),
    googleanalyticsid      VARCHAR(120),
    metapixelid            VARCHAR(120),
    colortema              VARCHAR(20),
    colorsecundario        VARCHAR(20),
    mantenimientoactivo    BOOLEAN      NOT NULL DEFAULT FALSE,
    mensajemantenimiento   VARCHAR(500),
    fechaactualizacion     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE imagengaleria (
    idimagengaleria    BIGSERIAL    PRIMARY KEY,
    idsede             BIGINT       NOT NULL REFERENCES sede(idsede),
    urlimagen          VARCHAR(500) NOT NULL,
    alttexto           VARCHAR(200),
    titulo             VARCHAR(150),
    descripcion        VARCHAR(300),
    categoriaimagen    VARCHAR(20)  NOT NULL
                           CHECK (categoriaimagen IN ('CUMPLEANOS', 'JUEGOS', 'DECORACION', 'GENERAL', 'EVENTO')),
    tipomime           VARCHAR(50),
    tamanobytes        BIGINT,
    ordenvisualizacion INT          NOT NULL DEFAULT 0,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    destacada          BOOLEAN      NOT NULL DEFAULT FALSE,
    eliminada          BOOLEAN      NOT NULL DEFAULT FALSE,
    idusuariosubio     BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechasubida        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_imagen_sede_cat  ON imagengaleria(idsede, categoriaimagen);
CREATE INDEX idx_imagen_orden     ON imagengaleria(ordenvisualizacion);
CREATE INDEX idx_imagen_destacada ON imagengaleria(destacada);

CREATE TABLE banner (
    idbanner         BIGSERIAL    PRIMARY KEY,
    idsede           BIGINT       REFERENCES sede(idsede),
    titulo           VARCHAR(200) NOT NULL,
    descripcion      VARCHAR(400),
    imagenurl        VARCHAR(500) NOT NULL,
    imagemovilurl    VARCHAR(500),
    enlacedestino    VARCHAR(500),
    textoboton       VARCHAR(80),
    coloroverlay     VARCHAR(20),
    tipobanner       VARCHAR(40)  NOT NULL DEFAULT 'HOME',
    fechainicio      DATE         NOT NULL,
    fechafin         DATE,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    orden            INT          NOT NULL DEFAULT 0,
    prioridad        INT          NOT NULL DEFAULT 0,
    solomovil        BOOLEAN      NOT NULL DEFAULT FALSE,
    solodesktop      BOOLEAN      NOT NULL DEFAULT FALSE,
    idusuariocreador BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_banner_fechas CHECK (fechafin IS NULL OR fechafin >= fechainicio)
);

CREATE INDEX idx_banner_fechas ON banner(fechainicio, fechafin);
CREATE INDEX idx_banner_activo ON banner(activo);
CREATE INDEX idx_banner_tipo   ON banner(tipobanner);

CREATE TABLE resena (
    idresena         BIGSERIAL    PRIMARY KEY,
    idcliente        BIGINT       REFERENCES cliente(idcliente),
    ideventoprivado  BIGINT       REFERENCES eventoprivado(ideventoprivado),
    nombreautor      VARCHAR(120) NOT NULL,
    contenido        TEXT         NOT NULL,
    calificacion     INT          NOT NULL CHECK (calificacion BETWEEN 1 AND 5),
    aprobada         BOOLEAN      NOT NULL DEFAULT FALSE,
    fotourl          VARCHAR(500),
    respuestaadmin   TEXT,
    fecharespuesta   TIMESTAMPTZ,
    destacada        BOOLEAN      NOT NULL DEFAULT FALSE,
    mostrarhome      BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioaprueba BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_resena_aprobada  ON resena(aprobada);
CREATE INDEX idx_resena_destacada ON resena(destacada);

CREATE TABLE faq (
    idfaq              BIGSERIAL    PRIMARY KEY,
    pregunta           VARCHAR(300) NOT NULL,
    respuesta          TEXT         NOT NULL,
    ordenvisualizacion INT          NOT NULL DEFAULT 0,
    visible            BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioeditor    BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE contenidolegal (
    idcontenidolegal   BIGSERIAL    PRIMARY KEY,
    tipo               VARCHAR(50)  NOT NULL UNIQUE,
    titulo             VARCHAR(200) NOT NULL,
    contenido          TEXT         NOT NULL,
    version            INT          NOT NULL DEFAULT 1,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioeditor    BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO contenidolegal (tipo, titulo, contenido) VALUES
    ('TERMINOS',   'Términos y Condiciones',  'Pendiente de redacción.'),
    ('PRIVACIDAD', 'Política de Privacidad',  'Pendiente de redacción.'),
    ('REEMBOLSO',  'Política de Reembolso',   'Pendiente de redacción.'),
    ('MENORES',    'Protección de Menores',   'Pendiente de redacción.')
ON CONFLICT DO NOTHING;
