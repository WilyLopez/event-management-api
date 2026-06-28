-- Migrar datos de sede → configuracion_publica (solo si el campo destino está vacío)
UPDATE configuracion_publica
SET
    telefono  = COALESCE(NULLIF(TRIM(configuracion_publica.telefono),  ''), (SELECT telefono  FROM sede WHERE id = 1)),
    correo    = COALESCE(NULLIF(TRIM(configuracion_publica.correo),    ''), (SELECT correo    FROM sede WHERE id = 1)),
    direccion = COALESCE(NULLIF(TRIM(configuracion_publica.direccion), ''), (SELECT direccion FROM sede WHERE id = 1))
WHERE EXISTS (SELECT 1 FROM sede WHERE id = 1);

-- Eliminar WHATSAPP_NUMERO de configuracion_global (ahora vive en configuracion_publica.whatsapp)
DELETE FROM configuracion_global WHERE clave = 'WHATSAPP_NUMERO';

-- Eliminar columnas de contacto de la tabla sede
ALTER TABLE sede DROP COLUMN IF EXISTS telefono;
ALTER TABLE sede DROP COLUMN IF EXISTS correo;
ALTER TABLE sede DROP COLUMN IF EXISTS direccion;
