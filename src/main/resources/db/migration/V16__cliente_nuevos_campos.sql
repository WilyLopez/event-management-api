-- V13__cliente_nuevos_campos.sql
-- Agrega campos para fotografia, ultimo login, fecha de nacimiento y tipo de cliente

ALTER TABLE cliente
  ADD COLUMN IF NOT EXISTS fotoperfil     VARCHAR(500),
  ADD COLUMN IF NOT EXISTS ultimologin    TIMESTAMPTZ,
  ADD COLUMN IF NOT EXISTS fechanacimiento DATE,
  ADD COLUMN IF NOT EXISTS tipocliente    VARCHAR(20)
                              DEFAULT 'PERSONA'
                              CHECK (tipocliente IN ('PERSONA', 'EMPRESA'));

-- Rellenar tipocliente en registros existentes:
-- Si tiene RUC → EMPRESA, de lo contrario → PERSONA
UPDATE cliente
SET tipocliente = CASE
  WHEN ruc IS NOT NULL AND ruc <> '' THEN 'EMPRESA'
  ELSE 'PERSONA'
END
WHERE tipocliente IS NULL;

-- Indice para mejorar consultas por tipo de cliente
CREATE INDEX IF NOT EXISTS idx_cliente_tipocliente ON cliente (tipocliente);
CREATE INDEX IF NOT EXISTS idx_cliente_ultimologin ON cliente (ultimologin DESC NULLS LAST);