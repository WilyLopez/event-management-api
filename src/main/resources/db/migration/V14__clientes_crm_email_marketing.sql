-- V14: CRM, clientes presenciales y email marketing

ALTER TABLE cliente
    ALTER COLUMN contresenahash DROP NOT NULL;

ALTER TABLE cliente
    ADD COLUMN IF NOT EXISTS origenregistro   VARCHAR(20)   NOT NULL DEFAULT 'WEB'
        CHECK (origenregistro IN ('WEB', 'PRESENCIAL', 'ADMIN')),
    ADD COLUMN IF NOT EXISTS tieneaccesoweb   BOOLEAN       NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS aceptacomunicaciones BOOLEAN   NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS observaciones    VARCHAR(500),
    ADD COLUMN IF NOT EXISTS fechamigracionweb TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS ultimavisita     TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS totalgastado     NUMERIC(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS segmentocliente  VARCHAR(30)   NOT NULL DEFAULT 'NUEVO'
        CHECK (segmentocliente IN ('NUEVO', 'FRECUENTE', 'VIP', 'CORPORATIVO', 'INACTIVO'));

CREATE INDEX IF NOT EXISTS idx_cliente_origen   ON cliente(origenregistro);
CREATE INDEX IF NOT EXISTS idx_cliente_segmento ON cliente(segmentocliente);
CREATE INDEX IF NOT EXISTS idx_cliente_acceso   ON cliente(tieneaccesoweb);

-- ─── Email marketing ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS tipoemail (
    idtipoemail BIGSERIAL    PRIMARY KEY,
    codigo      VARCHAR(50)  UNIQUE NOT NULL,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

INSERT INTO tipoemail (codigo, nombre, descripcion) VALUES
    ('PROMOCION',             'Promoción',             'Campañas promocionales'),
    ('CUMPLEANOS',            'Cumpleaños',             'Saludos automáticos'),
    ('EVENTO',                'Eventos',                'Eventos especiales'),
    ('BIENVENIDA',            'Bienvenida',             'Registro nuevo'),
    ('RECUPERACION_PASSWORD', 'Recuperación contraseña','Reset password'),
    ('RESERVA_CONFIRMADA',    'Reserva confirmada',     'Confirmación reserva')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS plantillaemail (
    idplantillaemail    BIGSERIAL    PRIMARY KEY,
    idtipoemail         BIGINT       NOT NULL REFERENCES tipoemail(idtipoemail),
    nombre              VARCHAR(120) NOT NULL,
    asunto              VARCHAR(200) NOT NULL,
    contenidohtml       TEXT         NOT NULL,
    contenidofallback   TEXT,
    variablespermitidas JSONB,
    activa              BOOLEAN      NOT NULL DEFAULT TRUE,
    idusuarioeditor     BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaactualizacion  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS campanaemail (
    idcampanaemail     BIGSERIAL    PRIMARY KEY,
    nombre             VARCHAR(150) NOT NULL,
    descripcion        VARCHAR(300),
    idplantillaemail   BIGINT       NOT NULL REFERENCES plantillaemail(idplantillaemail),
    estado             VARCHAR(30)  NOT NULL DEFAULT 'BORRADOR'
        CHECK (estado IN ('BORRADOR','PROGRAMADA','ENVIANDO','FINALIZADA','CANCELADA')),
    fechaprogramada    TIMESTAMPTZ,
    totaldestinatarios INT          NOT NULL DEFAULT 0,
    totalenviados      INT          NOT NULL DEFAULT 0,
    totalfallidos      INT          NOT NULL DEFAULT 0,
    idusuariocreador   BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS envioemail (
    idenvioemail       BIGSERIAL    PRIMARY KEY,
    idcampanaemail     BIGINT       REFERENCES campanaemail(idcampanaemail),
    idcliente          BIGINT       REFERENCES cliente(idcliente),
    destinatario       VARCHAR(120) NOT NULL,
    asunto             VARCHAR(200) NOT NULL,
    estado             VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE','ENVIADO','ERROR','REBOTADO')),
    intentos           INT          NOT NULL DEFAULT 0,
    fechaenvio         TIMESTAMPTZ,
    mensajeerror       VARCHAR(500),
    proveedormensajeid VARCHAR(200),
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_envio_estado   ON envioemail(estado);
CREATE INDEX IF NOT EXISTS idx_envio_cliente  ON envioemail(idcliente);
CREATE INDEX IF NOT EXISTS idx_envio_campana  ON envioemail(idcampanaemail);
