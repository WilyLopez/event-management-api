-- ============================================================
-- V21: Tabla programacion_semanal
-- Separa la apertura semanal de reservas publicas del sistema
-- de bloqueos. PLANIFICACION_SEMANAL sale de bloque_calendario.
-- ============================================================

CREATE TABLE programacion_semanal (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id         BIGINT          NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
    semana_inicio   DATE            NOT NULL,
    semana_fin      DATE            NOT NULL,
    estado          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVA',

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      UUID            REFERENCES perfil_usuario(id),

    CONSTRAINT ck_programacion_fechas  CHECK (semana_fin >= semana_inicio),
    CONSTRAINT ck_programacion_estado  CHECK (estado IN ('ACTIVA', 'CANCELADA')),
    CONSTRAINT uq_programacion_sede_semana UNIQUE (sede_id, semana_inicio)
);

CREATE INDEX idx_programacion_sede_rango
    ON programacion_semanal (sede_id, semana_inicio, semana_fin)
    WHERE estado = 'ACTIVA';

CREATE TRIGGER trg_programacion_semanal_updated_at
    BEFORE UPDATE ON programacion_semanal
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();

-- Migrar registros existentes de PLANIFICACION_SEMANAL
INSERT INTO programacion_semanal (sede_id, semana_inicio, semana_fin, estado, created_by, created_at)
SELECT sede_id, fecha_inicio, fecha_fin, 'ACTIVA', created_by, created_at
FROM   bloque_calendario
WHERE  tipo_bloqueo = 'PLANIFICACION_SEMANAL'
  AND  es_activo    = TRUE
  AND  fecha_fin    >= CURRENT_DATE;

-- Eliminar PLANIFICACION_SEMANAL de bloque_calendario
DELETE FROM bloque_calendario WHERE tipo_bloqueo = 'PLANIFICACION_SEMANAL';
