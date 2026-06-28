ALTER TABLE movimiento_caja
    ADD COLUMN IF NOT EXISTS categoria_retiro VARCHAR(30);
