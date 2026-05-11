-- V19: Extend usuarioadmin, logauditoria, and create sesionadmin

-- ─── Campos nuevos en usuarioadmin ───────────────────────────────────────────
ALTER TABLE usuarioadmin
    ADD COLUMN rol                    VARCHAR(30)  NOT NULL DEFAULT 'ADMINISTRATIVO',
    ADD COLUMN fotoperfilurl          VARCHAR(500),
    ADD COLUMN telefono               VARCHAR(20),
    ADD COLUMN debecambiarcontrasena  BOOLEAN      NOT NULL DEFAULT TRUE,
    ADD COLUMN ultimocambiocontrasena TIMESTAMPTZ,
    ADD COLUMN creado_por             BIGINT       REFERENCES usuarioadmin(idusuarioadmin);

-- ─── Campos nuevos en logauditoria ───────────────────────────────────────────
ALTER TABLE logauditoria
    ADD COLUMN nivel      VARCHAR(20) NOT NULL DEFAULT 'INFO',
    ADD COLUMN resultado  VARCHAR(20) NOT NULL DEFAULT 'EXITOSO';

-- ─── Tabla de sesiones administrativas ──────────────────────────────────────
CREATE TABLE sesionadmin (
    idsesion        BIGSERIAL    PRIMARY KEY,
    idusuarioadmin  BIGINT       NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    ip              INET,
    useragent       VARCHAR(300),
    tokenjti        VARCHAR(120),
    iniciologin     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    ultimactividad  TIMESTAMPTZ,
    cerrada         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_sesionadmin_usuario ON sesionadmin(idusuarioadmin);
CREATE INDEX idx_sesionadmin_activa  ON sesionadmin(idusuarioadmin, cerrada);
CREATE INDEX idx_logauditoria_nivel  ON logauditoria(nivel);
