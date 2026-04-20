-- V10: Auditoría del sistema

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE logauditoria (
    idlog            BIGSERIAL    PRIMARY KEY,
    idusuarioadmin   INT          REFERENCES usuarioadmin(idusuarioadmin),
    accion           VARCHAR(40)  NOT NULL,
    modulo           VARCHAR(80)  NOT NULL,
    entidadafectada  VARCHAR(80)  NOT NULL,
    identidad        INT,
    valoranterior    JSONB,
    valornuevo       JSONB,
    descripcion      VARCHAR(500),
    iporigen         INET,
    useragent        VARCHAR(300),
    timestamp        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_log_usuario        ON logauditoria(idusuarioadmin);
CREATE INDEX idx_log_timestamp      ON logauditoria(timestamp DESC);
CREATE INDEX idx_log_modulo_entidad ON logauditoria(modulo, entidadafectada);
CREATE INDEX idx_log_entidad_id     ON logauditoria(entidadafectada, identidad);