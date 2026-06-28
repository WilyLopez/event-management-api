CREATE TABLE IF NOT EXISTS tipo_legal (
    codigo          VARCHAR(50)  PRIMARY KEY,
    etiqueta        VARCHAR(120) NOT NULL,
    slug            VARCHAR(80)  NOT NULL UNIQUE,
    orden           INT          NOT NULL DEFAULT 100,
    es_sistema      BOOLEAN      NOT NULL DEFAULT FALSE,
    requerido       BOOLEAN      NOT NULL DEFAULT FALSE,
    visible_footer  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO tipo_legal (codigo, etiqueta, slug, orden, es_sistema, requerido, visible_footer) VALUES
    ('TERMINOS',   'Terminos y Condiciones',  'terminos',             10, TRUE,  TRUE,  TRUE),
    ('PRIVACIDAD', 'Politica de Privacidad',  'privacidad',           20, TRUE,  TRUE,  TRUE),
    ('MENORES',    'Proteccion de Menores',   'menores',              30, FALSE, TRUE,  TRUE),
    ('ACTA',       'Acta de Responsabilidad', 'acta-responsabilidad', 40, FALSE, TRUE,  TRUE),
    ('REEMBOLSO',  'Politica de Reembolso',   'reembolsos',           50, FALSE, FALSE, TRUE),
    ('COOKIES',    'Politica de Cookies',     'cookies',              60, FALSE, FALSE, TRUE)
ON CONFLICT (codigo) DO NOTHING;

CREATE TABLE IF NOT EXISTS contenido_legal_historial (
    id          BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    legal_id    BIGINT       NOT NULL REFERENCES contenido_legal (id) ON DELETE CASCADE,
    tipo        VARCHAR(50)  NOT NULL,
    titulo      VARCHAR(120) NOT NULL,
    contenido   TEXT         NOT NULL,
    version_v   INT          NOT NULL,
    created_by  UUID         REFERENCES perfil_usuario (id),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_legal_historial_legal_id
    ON contenido_legal_historial (legal_id, version_v DESC);

ALTER TABLE contenido_legal DROP CONSTRAINT IF EXISTS ck_legal_tipo;

INSERT INTO contenido_legal (tipo, titulo, contenido, version_v, es_activo, created_at, updated_at)
SELECT t.codigo,
       t.etiqueta,
       'Contenido pendiente de redaccion. Edite este documento desde el panel de administracion.',
       1,
       TRUE,
       NOW(),
       NOW()
FROM tipo_legal t
WHERE NOT EXISTS (
    SELECT 1 FROM contenido_legal c WHERE c.tipo = t.codigo
);

ALTER TABLE contenido_legal DROP CONSTRAINT IF EXISTS fk_contenido_legal_tipo;
ALTER TABLE contenido_legal
    ADD CONSTRAINT fk_contenido_legal_tipo
    FOREIGN KEY (tipo) REFERENCES tipo_legal (codigo);
