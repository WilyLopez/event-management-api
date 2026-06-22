-- Asignar permisos de visualización y creación de reservas al rol CLIENTE
INSERT INTO public.rol_permiso (rol_codigo, permiso_codigo) VALUES
    ('CLIENTE', 'reserva.crear'),
    ('CLIENTE', 'reserva.ver')
ON CONFLICT (rol_codigo, permiso_codigo) DO NOTHING;
