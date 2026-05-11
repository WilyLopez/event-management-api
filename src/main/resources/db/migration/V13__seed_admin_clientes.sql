-- V13: Datos iniciales — administrador Wilian Lopez y 5 clientes de prueba

-- ─── Administrador: Wilian Lopez ─────────────────────────────────────────────
-- Contraseña inicial: admin123 (BCrypt 10 rounds) — cambiar en primer acceso
INSERT INTO usuarioadmin (
    idsede,
    nombre,
    correo,
    contresenahash,
    rol,
    telefono,
    activo,
    debecambiarcontrasena
) VALUES (
    1,
    'Wilian Lopez',
    'wlopez@playzone.com',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7uSyLnS',
    'SUPERADMIN',
    '999000001',
    TRUE,
    TRUE
);

-- ─── 5 Clientes iniciales ─────────────────────────────────────────────────────
-- Contraseña inicial generada con pgcrypto: cliente123
INSERT INTO cliente (
    nombre,
    correo,
    contresenahash,
    telefono,
    dni,
    tipocliente,
    correoverificado,
    activo,
    fechanacimiento
) VALUES
    (
        'Carlos Mendoza Quispe',
        'cmendoza@gmail.com',
        crypt('cliente123', gen_salt('bf', 10)),
        '987654321',
        '72345678',
        'PERSONA',
        TRUE,
        TRUE,
        '1992-03-15'
    ),
    (
        'Ana Torres Huanca',
        'atorres@gmail.com',
        crypt('cliente123', gen_salt('bf', 10)),
        '976543210',
        '73456789',
        'PERSONA',
        TRUE,
        TRUE,
        '1995-07-22'
    ),
    (
        'Luis Ramírez Ccama',
        'lramirez@gmail.com',
        crypt('cliente123', gen_salt('bf', 10)),
        '965432109',
        '74567890',
        'PERSONA',
        TRUE,
        TRUE,
        '1988-11-08'
    ),
    (
        'María Flores Condori',
        'mflores@gmail.com',
        crypt('cliente123', gen_salt('bf', 10)),
        '954321098',
        '75678901',
        'PERSONA',
        TRUE,
        TRUE,
        '1998-05-30'
    ),
    (
        'Eventos Andinos SAC',
        'contacto@eventosandinos.com',
        crypt('cliente123', gen_salt('bf', 10)),
        '943210987',
        NULL,
        'EMPRESA',
        TRUE,
        TRUE,
        NULL
    );

-- Datos de empresa para el cliente corporativo
UPDATE cliente
SET ruc             = '20123456789',
    razonsocial     = 'Eventos Andinos SAC',
    direccionfiscal = 'Av. El Sol 123, Cusco'
WHERE correo = 'contacto@eventosandinos.com';
