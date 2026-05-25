INSERT INTO cliente (nombre, correo, contresenahash, telefono, activo, origenregistro, tipocliente)
VALUES (
    'Cliente Mostrador',
    'mostrador@kikiylala.com',
    '$2a$10$MOSTRADOR.DISABLED.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.',
    '000000000',
    true,
    'PRESENCIAL',
    'PERSONA'
)
ON CONFLICT (correo) DO NOTHING;
