-- Migración para añadir soporte dinámico a la página pública
ALTER TABLE configuracion_publica
ADD COLUMN metricas_negocio JSONB,
ADD COLUMN reglas_local JSONB;

COMMENT ON COLUMN configuracion_publica.metricas_negocio IS 'Estadísticas del negocio expuestas en la web en formato JSONB';
COMMENT ON COLUMN configuracion_publica.reglas_local IS 'Reglamento de seguridad y normas del local expuestos en la web en formato JSONB';
