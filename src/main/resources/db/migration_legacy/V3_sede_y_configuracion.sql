
CREATE TABLE sede (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          TEXT         NOT NULL,
    direccion       TEXT         NOT NULL,
    ciudad          TEXT         NOT NULL,
    departamento    TEXT         NOT NULL,
    telefono        TEXT,
    correo          CITEXT,
    ruc             TEXT,
    latitud         NUMERIC(10,7),
    longitud        NUMERIC(10,7),

    -- auditoría
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      UUID,                                
    updated_by      UUID,
    deleted_at      TIMESTAMPTZ,                         

    CONSTRAINT ck_sede_ruc_longitud CHECK (ruc IS NULL OR length(ruc) = 11),
    CONSTRAINT ck_sede_lat CHECK (latitud  IS NULL OR (latitud  BETWEEN -90  AND 90)),
    CONSTRAINT ck_sede_lng CHECK (longitud IS NULL OR (longitud BETWEEN -180 AND 180))
);

CREATE INDEX idx_sede_deleted_at ON sede (deleted_at) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_sede_updated_at BEFORE UPDATE ON sede FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();

COMMENT ON TABLE sede IS 'Locales físicos del negocio. Diseñado para multi-sede.';



CREATE TABLE configuracion_sede (
    id                                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id                             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE CASCADE,

    
    dias_min_reserva_publica            INT          NOT NULL DEFAULT 0,
    dias_max_reserva_publica            INT          NOT NULL DEFAULT 14,

    
    dias_min_evento_privado             INT          NOT NULL DEFAULT 15,
    dias_max_evento_privado             INT          NOT NULL DEFAULT 365,

    
    aforo_maximo                        INT          NOT NULL DEFAULT 60,
    hora_apertura                       TIME         NOT NULL DEFAULT '10:00',
    hora_cierre                         TIME         NOT NULL DEFAULT '20:00',

    
    dias_operacion                      INT[]        NOT NULL DEFAULT ARRAY[1,2,3,4,5,6,7]::INT[],

    
    rango_max_bloqueo_dias              INT          NOT NULL DEFAULT 90,

    
    
    max_reprogramaciones_por_entrada    INT          NOT NULL DEFAULT 1,

    visitas_para_entrada_gratis         INT          NOT NULL DEFAULT 6,


    porcentaje_adelanto_evento          NUMERIC(5,2) NOT NULL DEFAULT 50.00,

    created_at                          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by                          UUID,
    updated_by                          UUID,

    CONSTRAINT uk_configuracion_sede UNIQUE (sede_id),
    CONSTRAINT ck_cfg_dias_reserva    CHECK (dias_max_reserva_publica >= dias_min_reserva_publica),
    CONSTRAINT ck_cfg_dias_evento     CHECK (dias_max_evento_privado >= dias_min_evento_privado),
    CONSTRAINT ck_cfg_horario         CHECK (hora_cierre > hora_apertura),
    CONSTRAINT ck_cfg_aforo           CHECK (aforo_maximo > 0),
    CONSTRAINT ck_cfg_adelanto        CHECK (porcentaje_adelanto_evento BETWEEN 0 AND 100)
);

CREATE TRIGGER trg_configuracion_sede_updated_at
    BEFORE UPDATE ON configuracion_sede
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();

COMMENT ON TABLE configuracion_sede IS
    'Parámetros operativos por sede: ventanas de reserva, aforo, horarios. Fuente única.';
COMMENT ON COLUMN configuracion_sede.dias_operacion IS
    'Días de la semana en que opera. Formato ISO: 1=lun, 2=mar, ..., 7=dom.';


CREATE TABLE configuracion_global (
    id               BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clave            TEXT         NOT NULL UNIQUE,
    valor            TEXT         NOT NULL,
    descripcion      TEXT,
    tipo_dato        TEXT         NOT NULL DEFAULT 'TEXTO',
    es_sistema       BOOLEAN      NOT NULL DEFAULT TRUE,                  -
    es_secreto       BOOLEAN      NOT NULL DEFAULT FALSE,                 

    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by       UUID,

    CONSTRAINT ck_cfg_global_tipo CHECK (tipo_dato IN ('TEXTO','NUMERO','BOOLEANO','JSON'))
);

CREATE INDEX idx_configuracion_global_clave ON configuracion_global (clave);

CREATE TRIGGER trg_configuracion_global_updated_at
    BEFORE UPDATE ON configuracion_global
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();

COMMENT ON TABLE configuracion_global IS
    'Configuración técnica del sistema. NO por sede. Reemplaza la antigua configuracion_sistema.';
COMMENT ON COLUMN configuracion_global.es_secreto IS
    'TRUE para valores sensibles (API keys, tokens). RLS y UI deben respetar este flag.';



CREATE TABLE configuracion_publica (
    id                       BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    nombre_negocio           TEXT         NOT NULL,
    slogan                   TEXT,
    logo_path                TEXT,                                       -- path en Supabase Storage
    favicon_path             TEXT,

    telefono                 TEXT,
    telefono_secundario      TEXT,
    whatsapp                 TEXT,
    correo                   CITEXT,
    correo_secundario        CITEXT,
    direccion                TEXT,

    facebook_url             TEXT,
    instagram_url            TEXT,
    tiktok_url               TEXT,
    youtube_url              TEXT,
    google_maps_url          TEXT,

    horario_semana           TEXT,
    horario_fin_semana       TEXT,

    meta_title               TEXT,
    meta_description         TEXT,
    meta_keywords            TEXT,
    open_graph_title         TEXT,
    open_graph_description   TEXT,
    open_graph_image_path    TEXT,

    google_analytics_id      TEXT,
    meta_pixel_id            TEXT,

    color_primario           TEXT,
    color_secundario         TEXT,

    es_mantenimiento_activo  BOOLEAN      NOT NULL DEFAULT FALSE,
    mensaje_mantenimiento    TEXT,

    copyright_texto          TEXT,

    created_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by               UUID
);

CREATE UNIQUE INDEX uk_configuracion_publica_singleton ON configuracion_publica ((1));

CREATE TRIGGER trg_configuracion_publica_updated_at
    BEFORE UPDATE ON configuracion_publica
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();

COMMENT ON TABLE configuracion_publica IS
    'Singleton: datos del negocio expuestos en el sitio web público. Solo una fila permitida.';
COMMENT ON COLUMN configuracion_publica.logo_path IS
    'Path relativo en bucket Supabase Storage (ej. /branding/logo.png). NO URL completa.';