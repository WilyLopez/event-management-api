CREATE TABLE refresh_token (
    idrefreshtoken  BIGSERIAL    PRIMARY KEY,
    token           VARCHAR(255) NOT NULL UNIQUE,
    idusuario       BIGINT       NOT NULL,
    correo          VARCHAR(255) NOT NULL,
    tipo_usuario    VARCHAR(20)  NOT NULL CHECK (tipo_usuario IN ('ADMIN','CLIENTE')),
    fecha_creacion  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fecha_expira    TIMESTAMPTZ  NOT NULL,
    revocado        BOOLEAN      NOT NULL DEFAULT FALSE,
    ultimo_uso      TIMESTAMPTZ
);

CREATE INDEX idx_refresh_token_token   ON refresh_token (token);
CREATE INDEX idx_refresh_token_usuario ON refresh_token (idusuario, tipo_usuario);
