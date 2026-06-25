-- ============================================================
-- V22: Mejoras tabla programacion_semanal
-- 1. Corrige constraint UNIQUE para permitir reprogramar
--    semanas que fueron canceladas (partial index solo ACTIVA).
-- 2. Agrega columna auto_generada para distinguir programaciones
--    creadas por el admin vs las generadas automaticamente.
-- ============================================================

-- 1. Reemplazar UNIQUE constraint por partial unique index
ALTER TABLE programacion_semanal
    DROP CONSTRAINT IF EXISTS uq_programacion_sede_semana;

CREATE UNIQUE INDEX uq_programacion_activa_sede_semana
    ON programacion_semanal (sede_id, semana_inicio)
    WHERE estado = 'ACTIVA';

-- 2. Columna para trazabilidad de generacion automatica
ALTER TABLE programacion_semanal
    ADD COLUMN IF NOT EXISTS auto_generada BOOLEAN NOT NULL DEFAULT FALSE;
