INSERT INTO configuracion_global (clave, valor, tipo_dato, descripcion, es_secreto)
VALUES ('MAX_REPROGRAMACIONES', '1', 'NUMERO', 'Máximo de reprogramaciones permitidas por reserva', false)
ON CONFLICT (clave) DO NOTHING;
