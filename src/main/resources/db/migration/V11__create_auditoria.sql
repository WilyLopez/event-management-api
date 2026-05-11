-- V11: Auditoría del sistema
-- La columna se nombra directamente "fechalog" (evita la palabra reservada "timestamp").
-- Se incluyen "nivel" y "resultado" desde el inicio (no como parche posterior).

CREATE TABLE logauditoria (
    idlog           BIGSERIAL    PRIMARY KEY,
    idusuarioadmin  BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    accion          VARCHAR(40)  NOT NULL,
    modulo          VARCHAR(80)  NOT NULL,
    entidadafectada VARCHAR(80)  NOT NULL,
    identidad       BIGINT,
    valoranterior   JSONB,
    valornuevo      JSONB,
    descripcion     VARCHAR(500),
    iporigen        INET,
    useragent       VARCHAR(300),
    nivel           VARCHAR(20)  NOT NULL DEFAULT 'INFO',
    resultado       VARCHAR(20)  NOT NULL DEFAULT 'EXITOSO',
    fechalog        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_log_usuario        ON logauditoria(idusuarioadmin);
CREATE INDEX idx_log_fechalog       ON logauditoria(fechalog DESC);
CREATE INDEX idx_log_modulo_entidad ON logauditoria(modulo, entidadafectada);
CREATE INDEX idx_log_entidad_id     ON logauditoria(entidadafectada, identidad);
CREATE INDEX idx_log_nivel          ON logauditoria(nivel);
