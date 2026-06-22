ALTER TABLE staff_perfil
ADD COLUMN debe_cambiar_contrasena BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN intentos_fallidos INT NOT NULL DEFAULT 0,
ADD COLUMN bloqueado_hasta TIMESTAMPTZ;

-- Nuevos permisos para gestion de staff
INSERT INTO permiso (codigo, modulo, nombre, descripcion, orden) VALUES
    ('usuarios.ver',    'usuario', 'Ver usuarios administrativos', 'Listar personal del sistema', 130),
    ('usuarios.crear',  'usuario', 'Crear usuarios administrativos', 'Registrar nuevo personal', 131),
    ('usuarios.editar', 'usuario', 'Editar usuarios administrativos', 'Modificar o activar/desactivar personal', 132)
ON CONFLICT (codigo) DO NOTHING;

-- Asignar permisos a roles existentes
INSERT INTO rol_permiso (rol_codigo, permiso_codigo)
SELECT 'SUPERADMIN', 'usuarios.ver' UNION ALL
SELECT 'SUPERADMIN', 'usuarios.crear' UNION ALL
SELECT 'SUPERADMIN', 'usuarios.editar' UNION ALL
SELECT 'ADMIN', 'usuarios.ver' UNION ALL
SELECT 'ADMIN', 'usuarios.crear' UNION ALL
SELECT 'ADMIN', 'usuarios.editar'
ON CONFLICT (rol_codigo, permiso_codigo) DO NOTHING;
