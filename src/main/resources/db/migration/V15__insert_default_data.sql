-- V15: Datos iniciales de administrador y tarifas base

-- 1. Insertar un administrador por defecto (Contraseña: admin123)
-- El hash corresponde a: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7uSyLnS
INSERT INTO usuarioadmin (idsede, nombre, correo, contresenahash) 
VALUES (1, 'Administrador Sistema', 'admin@playzone.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7uSyLnS');

-- 2. Insertar tarifas base para la sede principal
INSERT INTO tarifa (idsede, idtipodiacod, precio, vigenciadesde, idusuariocreador)
VALUES 
    (1, 'SEMANA', 25.00, '2024-01-01', 1),
    (1, 'FIN_SEMANA_FERIADO', 35.00, '2024-01-01', 1);
