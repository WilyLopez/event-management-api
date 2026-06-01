CREATE TABLE servicio_cotizacion (
    idservicio          BIGSERIAL    PRIMARY KEY,
    nombre              VARCHAR(150) NOT NULL,
    descripcion         VARCHAR(300),
    precio_referencial  NUMERIC(10,2) NOT NULL DEFAULT 0,
    icono               VARCHAR(50),
    activo              BOOLEAN      NOT NULL DEFAULT TRUE,
    orden               INT          NOT NULL DEFAULT 0,
    fechacreacion       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO servicio_cotizacion (nombre, descripcion, precio_referencial, icono, orden) VALUES
    ('Decoración temática',  'Ambientación completa según la temática elegida', 250.00, 'palette',   1),
    ('Animación y show',     'Animador profesional y show de personajes',       300.00, 'sparkles',  2),
    ('Catering / comida',    'Bocaditos, dulces y bebidas para los invitados',  400.00, 'utensils',  3),
    ('Torta personalizada',  'Torta temática a medida',                          150.00, 'cake',      4),
    ('Sonido y música',      'Equipo de sonido y musicalización',                120.00, 'music',     5),
    ('Fotografía / video',   'Cobertura fotográfica del evento',                 200.00, 'camera',    6),
    ('Piñata',               'Piñata temática con relleno',                       80.00, 'gift',      7);

ALTER TABLE eventoprivado
    ADD COLUMN IF NOT EXISTS descripcion_personalizada TEXT,
    ADD COLUMN IF NOT EXISTS presupuesto_estimado       NUMERIC(10,2),
    ADD COLUMN IF NOT EXISTS es_cotizacion_personalizada BOOLEAN NOT NULL DEFAULT FALSE;
