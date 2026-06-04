
CREATE TABLE rol (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_rol_updated_at        BEFORE UPDATE ON rol FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_rol_no_codigo_sistema BEFORE UPDATE ON rol FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_rol_no_delete_sistema BEFORE DELETE ON rol FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();

COMMENT ON TABLE rol IS 'Roles del sistema. Un usuario puede tener varios (ver usuario_rol en V4).';


CREATE TABLE permiso (
    codigo       TEXT         PRIMARY KEY,                
    modulo       TEXT         NOT NULL,                   
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,      
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_permiso_codigo_formato CHECK (codigo ~ '^[a-z_]+\.[a-z_]+$') 
);

CREATE INDEX idx_permiso_modulo ON permiso (modulo);

CREATE TRIGGER trg_permiso_updated_at        BEFORE UPDATE ON permiso FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_permiso_no_codigo_sistema BEFORE UPDATE ON permiso FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_permiso_no_delete_sistema BEFORE DELETE ON permiso FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();

COMMENT ON TABLE permiso IS 'Permisos granulares con formato modulo.accion (ej. reserva.crear).';


CREATE TABLE rol_permiso (
    rol_codigo      TEXT NOT NULL REFERENCES rol(codigo)     ON UPDATE CASCADE ON DELETE CASCADE,
    permiso_codigo  TEXT NOT NULL REFERENCES permiso(codigo) ON UPDATE CASCADE ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (rol_codigo, permiso_codigo)
);

CREATE INDEX idx_rol_permiso_permiso ON rol_permiso (permiso_codigo);

COMMENT ON TABLE rol_permiso IS 'Mapeo many-to-many: cada rol agrupa N permisos.';


CREATE TABLE estado_reserva (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_terminal  BOOLEAN      NOT NULL DEFAULT FALSE,   
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_estado_reserva_updated_at        BEFORE UPDATE ON estado_reserva FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_estado_reserva_no_codigo_sistema BEFORE UPDATE ON estado_reserva FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_estado_reserva_no_delete_sistema BEFORE DELETE ON estado_reserva FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE estado_evento (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_terminal  BOOLEAN      NOT NULL DEFAULT FALSE,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_estado_evento_updated_at        BEFORE UPDATE ON estado_evento FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_estado_evento_no_codigo_sistema BEFORE UPDATE ON estado_evento FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_estado_evento_no_delete_sistema BEFORE DELETE ON estado_evento FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE estado_contrato (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_terminal  BOOLEAN      NOT NULL DEFAULT FALSE,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_estado_contrato_updated_at        BEFORE UPDATE ON estado_contrato FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_estado_contrato_no_codigo_sistema BEFORE UPDATE ON estado_contrato FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_estado_contrato_no_delete_sistema BEFORE DELETE ON estado_contrato FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE estado_comprobante (
    codigo       TEXT         PRIMARY KEY,               
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_terminal  BOOLEAN      NOT NULL DEFAULT FALSE,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_estado_comprobante_updated_at        BEFORE UPDATE ON estado_comprobante FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_estado_comprobante_no_codigo_sistema BEFORE UPDATE ON estado_comprobante FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_estado_comprobante_no_delete_sistema BEFORE DELETE ON estado_comprobante FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE estado_caja (
    codigo       TEXT         PRIMARY KEY,              
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_terminal  BOOLEAN      NOT NULL DEFAULT FALSE,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_estado_caja_updated_at        BEFORE UPDATE ON estado_caja FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_estado_caja_no_codigo_sistema BEFORE UPDATE ON estado_caja FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_estado_caja_no_delete_sistema BEFORE DELETE ON estado_caja FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();



CREATE TABLE medio_pago (
    codigo       TEXT         PRIMARY KEY,                
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_efectivo  BOOLEAN      NOT NULL DEFAULT FALSE,      
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE,     
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_medio_pago_updated_at        BEFORE UPDATE ON medio_pago FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_medio_pago_no_codigo_sistema BEFORE UPDATE ON medio_pago FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_medio_pago_no_delete_sistema BEFORE DELETE ON medio_pago FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();

COMMENT ON COLUMN medio_pago.es_efectivo IS
    'Si TRUE, los pagos de este medio impactan la caja (movimiento_caja). Si FALSE, solo registro_ingreso.';


CREATE TABLE tipo_dia (
    codigo       TEXT         PRIMARY KEY,                
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_dia_updated_at        BEFORE UPDATE ON tipo_dia FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_dia_no_codigo_sistema BEFORE UPDATE ON tipo_dia FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_dia_no_delete_sistema BEFORE DELETE ON tipo_dia FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE canal_reserva (
    codigo       TEXT         PRIMARY KEY,               
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_canal_reserva_updated_at        BEFORE UPDATE ON canal_reserva FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_canal_reserva_no_codigo_sistema BEFORE UPDATE ON canal_reserva FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_canal_reserva_no_delete_sistema BEFORE DELETE ON canal_reserva FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE turno (
    codigo       TEXT         PRIMARY KEY,               
    nombre       TEXT         NOT NULL,
    hora_inicio  TIME         NOT NULL,
    hora_fin     TIME         NOT NULL,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_turno_horas CHECK (hora_fin > hora_inicio)
);
CREATE TRIGGER trg_turno_updated_at        BEFORE UPDATE ON turno FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_turno_no_codigo_sistema BEFORE UPDATE ON turno FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_turno_no_delete_sistema BEFORE DELETE ON turno FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();

COMMENT ON TABLE turno IS 'Turnos para eventos privados. Las reservas públicas operan por hora libre.';


CREATE TABLE tipo_feriado (
    codigo       TEXT         PRIMARY KEY,      
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_feriado_updated_at        BEFORE UPDATE ON tipo_feriado FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_feriado_no_codigo_sistema BEFORE UPDATE ON tipo_feriado FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_feriado_no_delete_sistema BEFORE DELETE ON tipo_feriado FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_promocion (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE, 
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_promocion_updated_at        BEFORE UPDATE ON tipo_promocion FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_promocion_no_codigo_sistema BEFORE UPDATE ON tipo_promocion FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_promocion_no_delete_sistema BEFORE DELETE ON tipo_promocion FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_comprobante (
    codigo            TEXT         PRIMARY KEY,           
    nombre            TEXT         NOT NULL,
    descripcion       TEXT,
    es_electronico    BOOLEAN      NOT NULL DEFAULT TRUE,  
    requiere_ruc      BOOLEAN      NOT NULL DEFAULT FALSE, 
    es_sistema        BOOLEAN      NOT NULL DEFAULT TRUE,
    activo            BOOLEAN      NOT NULL DEFAULT TRUE,
    orden             INT          NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_comprobante_updated_at        BEFORE UPDATE ON tipo_comprobante FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_comprobante_no_codigo_sistema BEFORE UPDATE ON tipo_comprobante FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_comprobante_no_delete_sistema BEFORE DELETE ON tipo_comprobante FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_documento (
    codigo       TEXT         PRIMARY KEY,                 
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    longitud     INT,                                      
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_documento_updated_at        BEFORE UPDATE ON tipo_documento FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_documento_no_codigo_sistema BEFORE UPDATE ON tipo_documento FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_documento_no_delete_sistema BEFORE DELETE ON tipo_documento FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_ingreso (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE,     
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_ingreso_updated_at        BEFORE UPDATE ON tipo_ingreso FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_ingreso_no_codigo_sistema BEFORE UPDATE ON tipo_ingreso FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_ingreso_no_delete_sistema BEFORE DELETE ON tipo_ingreso FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_egreso (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    categoria    TEXT         NOT NULL,                   
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_tipo_egreso_categoria CHECK (categoria IN ('RECURRENTE_FIJO','RECURRENTE_VARIABLE','EVENTUAL'))
);
CREATE TRIGGER trg_tipo_egreso_updated_at        BEFORE UPDATE ON tipo_egreso FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_egreso_no_codigo_sistema BEFORE UPDATE ON tipo_egreso FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_egreso_no_delete_sistema BEFORE DELETE ON tipo_egreso FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_evento (
    codigo       TEXT         PRIMARY KEY,                
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    icono        TEXT,                                     
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_evento_updated_at        BEFORE UPDATE ON tipo_evento FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_evento_no_codigo_sistema BEFORE UPDATE ON tipo_evento FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_evento_no_delete_sistema BEFORE DELETE ON tipo_evento FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();



CREATE TABLE tipo_notificacion (
    codigo               TEXT         PRIMARY KEY,    
    modulo               TEXT         NOT NULL,       
    nombre               TEXT         NOT NULL,
    descripcion          TEXT,
    destinatario_default TEXT         NOT NULL,          
    canales_default      TEXT[]       NOT NULL DEFAULT ARRAY['IN_APP']::TEXT[],   
    plantilla_titulo     TEXT,
    plantilla_mensaje    TEXT,
    prioridad            TEXT         NOT NULL DEFAULT 'NORMAL', 
    es_sistema           BOOLEAN      NOT NULL DEFAULT FALSE,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    orden                INT          NOT NULL DEFAULT 0,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_notif_destinatario CHECK (destinatario_default IN ('ADMIN','CAJERO','CLIENTE','SISTEMA')),
    CONSTRAINT ck_notif_prioridad    CHECK (prioridad IN ('BAJA','NORMAL','ALTA','CRITICA'))
);
CREATE TRIGGER trg_tipo_notif_updated_at        BEFORE UPDATE ON tipo_notificacion FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_notif_no_codigo_sistema BEFORE UPDATE ON tipo_notificacion FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_notif_no_delete_sistema BEFORE DELETE ON tipo_notificacion FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();

COMMENT ON COLUMN tipo_notificacion.canales_default IS
    'Array de canales por defecto. El usuario puede sobreescribir vía preferencia_notificacion.';


CREATE TABLE seccion_web (
    codigo       TEXT         PRIMARY KEY, 
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_seccion_web_updated_at        BEFORE UPDATE ON seccion_web FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_seccion_web_no_codigo_sistema BEFORE UPDATE ON seccion_web FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_seccion_web_no_delete_sistema BEFORE DELETE ON seccion_web FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_contenido (
    codigo       TEXT         PRIMARY KEY,
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT TRUE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_contenido_updated_at        BEFORE UPDATE ON tipo_contenido FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_contenido_no_codigo_sistema BEFORE UPDATE ON tipo_contenido FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_contenido_no_delete_sistema BEFORE DELETE ON tipo_contenido FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE tipo_email (
    codigo       TEXT         PRIMARY KEY,                 -- TRANSACCIONAL, MARKETING, RECORDATORIO
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_tipo_email_updated_at        BEFORE UPDATE ON tipo_email FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_tipo_email_no_codigo_sistema BEFORE UPDATE ON tipo_email FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_tipo_email_no_delete_sistema BEFORE DELETE ON tipo_email FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();


CREATE TABLE segmento_cliente (
    codigo       TEXT         PRIMARY KEY,                 -- NUEVO, FRECUENTE, VIP, CORPORATIVO, INACTIVO
    nombre       TEXT         NOT NULL,
    descripcion  TEXT,
    es_sistema   BOOLEAN      NOT NULL DEFAULT FALSE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    orden        INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE TRIGGER trg_segmento_cliente_updated_at        BEFORE UPDATE ON segmento_cliente FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
CREATE TRIGGER trg_segmento_cliente_no_codigo_sistema BEFORE UPDATE ON segmento_cliente FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_codigo_change();
CREATE TRIGGER trg_segmento_cliente_no_delete_sistema BEFORE DELETE ON segmento_cliente FOR EACH ROW EXECUTE FUNCTION app.prevent_sistema_delete();