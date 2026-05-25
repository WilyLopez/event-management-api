INSERT INTO cliente (nombre, correo, contresenahash, telefono, activo, origenregistro, tipocliente)
VALUES (
    'Cliente Mostrador',
    'mostrador@kikiylala.com',
    '$2a$12$KsyCuSK.8ikM4JLvNcm16ewlI.ZAii.cg/vL2re3Spr4y/v4PEIfa',
    '000000000',
    false,
    'PRESENCIAL',
    'PERSONA'
)
ON CONFLICT (correo) DO NOTHING;
