-- Índices compuestos para consultas de rango por sede + fecha en módulo de finanzas
CREATE INDEX IF NOT EXISTS idx_registro_ingreso_fecha
    ON registro_ingreso (sede_id, fecha);

CREATE INDEX IF NOT EXISTS idx_registro_egreso_fecha
    ON registro_egreso (sede_id, fecha);

-- Auditoría de eliminación lógica
ALTER TABLE registro_ingreso ADD COLUMN IF NOT EXISTS deleted_by UUID;
