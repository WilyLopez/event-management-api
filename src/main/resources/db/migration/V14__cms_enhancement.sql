-- V14: CMS enhancement – secciones, tipos, configuración pública, FAQ y contenido legal

CREATE TABLE IF NOT EXISTS seccionweb (
    idseccion          BIGSERIAL    PRIMARY KEY,
    codigo             VARCHAR(50)  UNIQUE NOT NULL,
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


CREATE TABLE IF NOT EXISTS tipocontenido (
    idtipocontenido BIGSERIAL   PRIMARY KEY,
    codigo          VARCHAR(40) UNIQUE NOT NULL,
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


ALTER TABLE contenidoweb
    ADD COLUMN IF NOT EXISTS imagenurl          VARCHAR(500),
    ADD COLUMN IF NOT EXISTS descripcion        VARCHAR(300),
    ADD COLUMN IF NOT EXISTS ordenvisualizacion INT     NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS visible            BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS version            INT     NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS metadatos          JSONB;

CREATE INDEX IF NOT EXISTS idx_contenido_clave           ON contenidoweb(clave);
CREATE INDEX IF NOT EXISTS idx_contenido_visible         ON contenidoweb(visible);
CREATE INDEX IF NOT EXISTS idx_contenido_seccion_visible ON contenidoweb(idseccion, visible);


CREATE TABLE IF NOT EXISTS configuracionpublica (
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


ALTER TABLE banner
    ADD COLUMN IF NOT EXISTS tipobanner    VARCHAR(40)  NOT NULL DEFAULT 'HOME',
    ADD COLUMN IF NOT EXISTS imagemovilurl VARCHAR(500),
    ADD COLUMN IF NOT EXISTS textoboton    VARCHAR(80),
    ADD COLUMN IF NOT EXISTS coloroverlay  VARCHAR(20),
    ADD COLUMN IF NOT EXISTS prioridad     INT          NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS solomovil    BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS solodesktop  BOOLEAN      NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_banner_tipo ON banner(tipobanner);


ALTER TABLE imagengaleria
    ADD COLUMN IF NOT EXISTS titulo       VARCHAR(150),
    ADD COLUMN IF NOT EXISTS descripcion  VARCHAR(300),
    ADD COLUMN IF NOT EXISTS tipomime     VARCHAR(50),
    ADD COLUMN IF NOT EXISTS tamanobytes  BIGINT,
    ADD COLUMN IF NOT EXISTS destacada    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS eliminada    BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_imagen_destacada ON imagengaleria(destacada);


ALTER TABLE resena
    ADD COLUMN IF NOT EXISTS ideventoprivado  BIGINT      REFERENCES eventoprivado(ideventoprivado),
    ADD COLUMN IF NOT EXISTS fotourl          VARCHAR(500),
    ADD COLUMN IF NOT EXISTS respuestaadmin   TEXT,
    ADD COLUMN IF NOT EXISTS fecharespuesta   TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS destacada        BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS mostrarhome      BOOLEAN     NOT NULL DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS idx_resena_destacada ON resena(destacada);


CREATE TABLE IF NOT EXISTS faq (
    idfaq              BIGSERIAL    PRIMARY KEY,
    pregunta           VARCHAR(300) NOT NULL,
    respuesta          TEXT         NOT NULL,
    ordenvisualizacion INT          NOT NULL DEFAULT 0,
    visible            BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioeditor    BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS contenidolegal (
    idcontenidolegal   BIGSERIAL    PRIMARY KEY,
    tipo               VARCHAR(50)  UNIQUE NOT NULL,
    titulo             VARCHAR(200) NOT NULL,
    contenido          TEXT         NOT NULL,
    version            INT          NOT NULL DEFAULT 1,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioeditor    BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO contenidolegal (tipo, titulo, contenido) VALUES
('TERMINOS',    'Términos y condiciones',   'Pendiente de redacción.'),
('PRIVACIDAD',  'Política de privacidad',   'Pendiente de redacción.'),
('REEMBOLSO',   'Política de reembolso',    'Pendiente de redacción.'),
('MENORES',     'Consentimiento de menores','Pendiente de redacción.')
ON CONFLICT DO NOTHING;
