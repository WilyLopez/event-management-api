-- Asignar permisos de edición, reprogramación y cancelación al rol CLIENTE
INSERT INTO public.rol_permiso (rol_codigo, permiso_codigo) VALUES
    ('CLIENTE', 'reserva.editar'),
    ('CLIENTE', 'reserva.reprogramar'),
    ('CLIENTE', 'reserva.cancelar')
ON CONFLICT (rol_codigo, permiso_codigo) DO NOTHING;
