ALTER TABLE paquete
    ADD COLUMN IF NOT EXISTS tipo_evento_codigo TEXT
        REFERENCES tipo_evento(codigo) ON DELETE SET NULL;
