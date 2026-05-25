INSERT INTO mediopago (codigo, descripcion)
VALUES ('CAJA', 'Pago en caja (presencial)')
ON CONFLICT (codigo) DO NOTHING;
