-- ============================================================
-- V23: Nuevo permiso calendario.programar
-- Permite gestionar la programacion semanal (apertura de
-- reservas publicas por semana). Se asigna a SUPERADMIN y ADMIN.
-- ============================================================

INSERT INTO permiso (codigo, modulo, nombre, descripcion, orden)
VALUES ('calendario.programar', 'calendario', 'Programar semanas',
        'Activar o cancelar la apertura publica de reservas por semana', 24);

INSERT INTO rol_permiso (rol_codigo, permiso_codigo) VALUES
    ('SUPERADMIN', 'calendario.programar'),
    ('ADMIN',      'calendario.programar');
