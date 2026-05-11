-- V2: Configuración del sistema y sede principal

CREATE TABLE configuracionsistema (
    idconfiguracion    BIGSERIAL    PRIMARY KEY,
    clave              VARCHAR(80)  NOT NULL UNIQUE,
    valor              VARCHAR(500) NOT NULL,
    descripcion        VARCHAR(300),
    tipo               VARCHAR(20)  NOT NULL DEFAULT 'TEXTO'
                           CHECK (tipo IN ('TEXTO', 'NUMERO', 'BOOLEANO', 'JSON')),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO configuracionsistema (clave, valor, tipo, descripcion) VALUES
    ('HORA_APERTURA',                      '10:00',    'TEXTO',  'Hora de apertura del local'),
    ('HORA_CIERRE',                        '20:00',    'TEXTO',  'Hora de cierre del local'),
    ('AFORO_MAXIMO',                       '60',       'NUMERO', 'Capacidad máxima del local'),
    ('ANTICIPACION_MIN_RESERVA_PUBLICA_H', '1',        'NUMERO', 'Anticipación mínima en horas para reserva pública'),
    ('ANTICIPACION_MIN_EVENTO_PRIVADO_D',  '15',       'NUMERO', 'Anticipación mínima en días para evento privado'),
    ('PLAZO_REPROGRAMACION_H',             '48',       'NUMERO', 'Horas límite para reprogramar una reserva'),
    ('MAX_REPROGRAMACIONES_POR_ENTRADA',   '1',        'NUMERO', 'Máximo de reprogramaciones por entrada'),
    ('VISITAS_PARA_ENTRADA_GRATIS',        '6',        'NUMERO', 'Visitas para obtener entrada gratuita'),
    ('INTENTOS_LOGIN_ANTES_BLOQUEO',       '5',        'NUMERO', 'Intentos fallidos antes del bloqueo'),
    ('DURACION_BLOQUEO_LOGIN_MIN',         '15',       'NUMERO', 'Duración del bloqueo en minutos'),
    ('EXPIRACION_SESION_ADMIN_MIN',        '30',       'NUMERO', 'Minutos de inactividad para expirar sesión'),
    ('PSE_PROVEEDOR',                      'NUBEFACT', 'TEXTO',  'Proveedor de servicios electrónicos SUNAT'),
    ('INTERVALO_PREPARACION_INICIO',       '14:00',    'TEXTO',  'Inicio del intervalo de preparación entre turnos'),
    ('INTERVALO_PREPARACION_FIN',          '16:00',    'TEXTO',  'Fin del intervalo de preparación entre turnos');

CREATE TABLE sede (
    idsede        BIGSERIAL    PRIMARY KEY,
    nombre        VARCHAR(120) NOT NULL,
    direccion     VARCHAR(300) NOT NULL,
    ciudad        VARCHAR(80)  NOT NULL,
    departamento  VARCHAR(80)  NOT NULL,
    telefono      VARCHAR(20),
    correo        VARCHAR(120),
    ruc           VARCHAR(11),
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    fechacreacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO sede (nombre, direccion, ciudad, departamento)
VALUES ('PlayZone Principal', 'Por definir', 'Por definir', 'Por definir');
