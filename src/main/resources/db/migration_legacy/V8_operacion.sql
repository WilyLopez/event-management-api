CREATE TABLE venta (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
    cliente_id          BIGINT       REFERENCES cliente_perfil(id) ON DELETE SET NULL,
    evento_id           BIGINT,
    tipo                TEXT         NOT NULL,
    canal_codigo        TEXT         NOT NULL REFERENCES canal_reserva(codigo) ON UPDATE CASCADE,

    fecha_visita        DATE,
    nombre_acompanante  TEXT,
    dni_acompanante     TEXT,
    telefono_acompanante TEXT,

    promocion_id        BIGINT       REFERENCES promocion(id) ON DELETE SET NULL,
    subtotal            NUMERIC(10,2) NOT NULL,
    descuento           NUMERIC(10,2) NOT NULL DEFAULT 0,
    total               NUMERIC(10,2) NOT NULL,
    efectivo_recibido   NUMERIC(10,2) NOT NULL DEFAULT 0,
    vuelto              NUMERIC(10,2) NOT NULL DEFAULT 0,

    acta_firmada        BOOLEAN      NOT NULL DEFAULT FALSE,
    es_anticipada       BOOLEAN      NOT NULL DEFAULT FALSE,
    notas               TEXT,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          UUID         NOT NULL REFERENCES perfil_usuario(id),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_venta_tipo      CHECK (tipo IN ('RESERVA','ADELANTO_EVENTO','SALDO_EVENTO','OTRO')),
    CONSTRAINT ck_venta_montos    CHECK (subtotal >= 0 AND descuento >= 0 AND total >= 0),
    CONSTRAINT ck_venta_total     CHECK (total = subtotal - descuento),
    CONSTRAINT ck_venta_efectivo  CHECK (efectivo_recibido >= 0 AND vuelto >= 0),
    CONSTRAINT ck_venta_evento    CHECK (
        (tipo IN ('ADELANTO_EVENTO','SALDO_EVENTO') AND evento_id IS NOT NULL)
        OR (tipo NOT IN ('ADELANTO_EVENTO','SALDO_EVENTO') AND evento_id IS NULL)
    ),
    CONSTRAINT ck_venta_fecha     CHECK (tipo <> 'RESERVA' OR fecha_visita IS NOT NULL)
);

CREATE INDEX idx_venta_sede_fecha   ON venta (sede_id, fecha_visita) WHERE deleted_at IS NULL;
CREATE INDEX idx_venta_cliente      ON venta (cliente_id)            WHERE cliente_id IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_venta_evento       ON venta (evento_id)             WHERE evento_id  IS NOT NULL;
CREATE INDEX idx_venta_tipo         ON venta (tipo);
CREATE INDEX idx_venta_created_at   ON venta (created_at DESC)       WHERE deleted_at IS NULL;

CREATE TRIGGER trg_venta_updated_at
    BEFORE UPDATE ON venta
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE venta_pago (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    venta_id            BIGINT       NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
    medio_pago_codigo   TEXT         NOT NULL REFERENCES medio_pago(codigo) ON UPDATE CASCADE,
    monto               NUMERIC(10,2) NOT NULL,
    referencia          TEXT,
    es_validado         BOOLEAN      NOT NULL DEFAULT TRUE,
    validado_por        UUID         REFERENCES perfil_usuario(id),
    validado_at         TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_venta_pago_monto CHECK (monto > 0)
);

CREATE INDEX idx_venta_pago_venta  ON venta_pago (venta_id);
CREATE INDEX idx_venta_pago_medio  ON venta_pago (medio_pago_codigo);
CREATE INDEX idx_venta_pago_no_val ON venta_pago (es_validado) WHERE es_validado = FALSE;


CREATE TABLE evento (
    id                          BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id                  BIGINT       REFERENCES cliente_perfil(id) ON DELETE RESTRICT,
    sede_id                     BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
    estado_codigo               TEXT         NOT NULL REFERENCES estado_evento(codigo) ON UPDATE CASCADE,
    turno_codigo                TEXT         NOT NULL REFERENCES turno(codigo) ON UPDATE CASCADE,
    tipo_evento_codigo          TEXT         NOT NULL REFERENCES tipo_evento(codigo) ON UPDATE CASCADE,

    fecha_evento                DATE         NOT NULL,
    contacto_adicional          TEXT,

    paquete_id                  BIGINT       REFERENCES paquete(id) ON DELETE SET NULL,
    es_cotizacion_personalizada BOOLEAN      NOT NULL DEFAULT FALSE,
    descripcion_personalizada   TEXT,
    presupuesto_estimado        NUMERIC(10,2),

    nombre_nino                 TEXT,
    edad_cumple                 INT,

    aforo_declarado             INT,
    precio_contrato             NUMERIC(10,2),
    monto_adelanto              NUMERIC(10,2) NOT NULL DEFAULT 0,

    estado_operativo            TEXT,
    checklist_completo          BOOLEAN      NOT NULL DEFAULT FALSE,
    hora_inicio_real            TIMESTAMPTZ,
    hora_fin_real               TIMESTAMPTZ,
    motivo_cancelacion          TEXT,
    notas_internas              TEXT,
    usuario_gestor_id           UUID         REFERENCES perfil_usuario(id),

    created_at                  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by                  UUID         REFERENCES perfil_usuario(id),
    updated_by                  UUID         REFERENCES perfil_usuario(id),
    deleted_at                  TIMESTAMPTZ,

    CONSTRAINT uk_evento_sede_fecha_turno UNIQUE (sede_id, fecha_evento, turno_codigo),
    CONSTRAINT ck_evento_aforo       CHECK (aforo_declarado    IS NULL OR aforo_declarado    > 0),
    CONSTRAINT ck_evento_precio      CHECK (precio_contrato    IS NULL OR precio_contrato    >= 0),
    CONSTRAINT ck_evento_adelanto    CHECK (monto_adelanto >= 0),
    CONSTRAINT ck_evento_adel_total  CHECK (precio_contrato IS NULL OR monto_adelanto <= precio_contrato),
    CONSTRAINT ck_evento_edad_cumple CHECK (edad_cumple IS NULL OR edad_cumple BETWEEN 0 AND 99),
    CONSTRAINT ck_evento_operativo   CHECK (estado_operativo IS NULL OR estado_operativo IN
        ('PENDIENTE_LOGISTICA','EN_PREPARACION','LISTO','EN_CURSO','FINALIZADO'))
);

CREATE INDEX idx_evento_cliente     ON evento (cliente_id)                          WHERE cliente_id IS NOT NULL;
CREATE INDEX idx_evento_sede_fecha  ON evento (sede_id, fecha_evento)               WHERE deleted_at IS NULL;
CREATE INDEX idx_evento_estado      ON evento (estado_codigo);
CREATE INDEX idx_evento_paquete     ON evento (paquete_id)                          WHERE paquete_id IS NOT NULL;

CREATE TRIGGER trg_evento_updated_at
    BEFORE UPDATE ON evento
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


ALTER TABLE venta
    ADD CONSTRAINT fk_venta_evento
    FOREIGN KEY (evento_id) REFERENCES evento(id) ON DELETE RESTRICT;

ALTER TABLE resena
    ADD CONSTRAINT fk_resena_evento
    FOREIGN KEY (evento_id) REFERENCES evento(id) ON DELETE SET NULL;


CREATE TABLE evento_extra (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id           BIGINT       NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    paquete_extra_id    BIGINT       REFERENCES paquete_extra(id) ON DELETE SET NULL,
    nombre_libre        TEXT,
    cantidad            INT          NOT NULL DEFAULT 1,
    notas               TEXT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_evento_extra_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_evento_extra_origen   CHECK (paquete_extra_id IS NOT NULL OR nombre_libre IS NOT NULL)
);

CREATE INDEX idx_evento_extra_evento ON evento_extra (evento_id);


CREATE TABLE evento_servicio (
    id                      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id               BIGINT       NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    servicio_cotizacion_id  BIGINT       REFERENCES servicio_cotizacion(id) ON DELETE SET NULL,
    nombre_libre            TEXT,
    descripcion             TEXT,
    precio_acordado         NUMERIC(10,2),
    incluido                BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_evento_servicio_origen CHECK (servicio_cotizacion_id IS NOT NULL OR nombre_libre IS NOT NULL),
    CONSTRAINT ck_evento_servicio_precio CHECK (precio_acordado IS NULL OR precio_acordado >= 0)
);

CREATE INDEX idx_evento_servicio_evento ON evento_servicio (evento_id);


CREATE TABLE checklist_evento (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id           BIGINT       NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    tarea               TEXT         NOT NULL,
    completada          BOOLEAN      NOT NULL DEFAULT FALSE,
    orden               INT          NOT NULL DEFAULT 0,
    completada_por      UUID         REFERENCES perfil_usuario(id),
    completada_at       TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_checklist_evento_evento ON checklist_evento (evento_id, orden);


CREATE TABLE contrato (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id           BIGINT       NOT NULL UNIQUE REFERENCES evento(id) ON DELETE CASCADE,
    estado_codigo       TEXT         NOT NULL REFERENCES estado_contrato(codigo) ON UPDATE CASCADE,
    contenido_texto     TEXT,
    archivo_pdf_path    TEXT,
    fecha_firma         DATE,
    version             INT          NOT NULL DEFAULT 1,
    plantilla           TEXT,
    observaciones       TEXT,
    redactor_id         UUID         REFERENCES perfil_usuario(id),

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by          UUID         REFERENCES perfil_usuario(id),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_contrato_origen CHECK (contenido_texto IS NOT NULL OR archivo_pdf_path IS NOT NULL)
);

CREATE INDEX idx_contrato_estado ON contrato (estado_codigo);

CREATE TRIGGER trg_contrato_updated_at
    BEFORE UPDATE ON contrato
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE contrato_documento (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contrato_id         BIGINT       NOT NULL REFERENCES contrato(id) ON DELETE CASCADE,
    nombre              TEXT         NOT NULL,
    archivo_path        TEXT         NOT NULL,
    tipo_archivo        TEXT         NOT NULL,
    tamano_bytes        BIGINT,
    subido_por          UUID         NOT NULL REFERENCES perfil_usuario(id),
    subido_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contrato_documento_contrato ON contrato_documento (contrato_id);


CREATE TABLE contrato_actividad (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contrato_id     BIGINT       NOT NULL REFERENCES contrato(id) ON DELETE CASCADE,
    accion          TEXT         NOT NULL,
    descripcion     TEXT,
    usuario_id      UUID         NOT NULL REFERENCES perfil_usuario(id),
    accion_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contrato_actividad_contrato ON contrato_actividad (contrato_id, accion_at DESC);


CREATE TABLE reserva (
    id                      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    venta_id                BIGINT       NOT NULL REFERENCES venta(id) ON DELETE RESTRICT,
    cliente_id              BIGINT       REFERENCES cliente_perfil(id) ON DELETE SET NULL,
    sede_id                 BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
    estado_codigo           TEXT         NOT NULL REFERENCES estado_reserva(codigo) ON UPDATE CASCADE,
    canal_codigo            TEXT         NOT NULL REFERENCES canal_reserva(codigo) ON UPDATE CASCADE,
    tipo_dia_codigo         TEXT         NOT NULL REFERENCES tipo_dia(codigo) ON UPDATE CASCADE,

    fecha_evento            DATE         NOT NULL,
    numero_ticket           TEXT         NOT NULL UNIQUE,
    codigo_qr               TEXT         UNIQUE,

    precio_historico        NUMERIC(10,2) NOT NULL,
    descuento_aplicado      NUMERIC(10,2) NOT NULL DEFAULT 0,
    total_pagado            NUMERIC(10,2) NOT NULL,

    nombre_nino             TEXT         NOT NULL,
    edad_nino               INT          NOT NULL,
    nombre_acompanante      TEXT,
    dni_acompanante         TEXT,

    firmo_consentimiento    BOOLEAN      NOT NULL DEFAULT FALSE,
    ingresado               BOOLEAN      NOT NULL DEFAULT FALSE,
    ingreso_at              TIMESTAMPTZ,

    reprogramada_desde_id   BIGINT       REFERENCES reserva(id) ON DELETE SET NULL,
    es_reprogramacion       BOOLEAN      NOT NULL DEFAULT FALSE,
    veces_reprogramada      INT          NOT NULL DEFAULT 0,
    motivo_cancelacion      TEXT,

    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by              UUID         REFERENCES perfil_usuario(id),
    updated_by              UUID         REFERENCES perfil_usuario(id),
    deleted_at              TIMESTAMPTZ,

    CONSTRAINT ck_reserva_montos        CHECK (precio_historico >= 0 AND descuento_aplicado >= 0 AND total_pagado >= 0),
    CONSTRAINT ck_reserva_total         CHECK (total_pagado = precio_historico - descuento_aplicado),
    CONSTRAINT ck_reserva_edad          CHECK (edad_nino BETWEEN 0 AND 17),
    CONSTRAINT ck_reserva_reprog        CHECK (es_reprogramacion = FALSE OR reprogramada_desde_id IS NOT NULL),
    CONSTRAINT ck_reserva_veces_reprog  CHECK (veces_reprogramada >= 0)
);

CREATE INDEX idx_reserva_venta        ON reserva (venta_id);
CREATE INDEX idx_reserva_cliente      ON reserva (cliente_id)        WHERE cliente_id IS NOT NULL;
CREATE INDEX idx_reserva_sede_fecha   ON reserva (sede_id, fecha_evento) WHERE deleted_at IS NULL;
CREATE INDEX idx_reserva_estado       ON reserva (estado_codigo);
CREATE INDEX idx_reserva_ingresado    ON reserva (ingresado) WHERE ingresado = TRUE;
CREATE INDEX idx_reserva_qr           ON reserva (codigo_qr) WHERE codigo_qr IS NOT NULL;
CREATE INDEX idx_reserva_reprog       ON reserva (reprogramada_desde_id) WHERE reprogramada_desde_id IS NOT NULL;

CREATE TRIGGER trg_reserva_updated_at
    BEFORE UPDATE ON reserva
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE reserva_promocion (
    reserva_id              BIGINT       NOT NULL REFERENCES reserva(id) ON DELETE CASCADE,
    promocion_id            BIGINT       NOT NULL REFERENCES promocion(id) ON DELETE RESTRICT,
    monto_descuento         NUMERIC(10,2) NOT NULL,
    aplicada_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (reserva_id, promocion_id),
    CONSTRAINT ck_reserva_promo_monto CHECK (monto_descuento >= 0)
);

CREATE INDEX idx_reserva_promocion_promocion ON reserva_promocion (promocion_id);


CREATE TABLE fidelizacion (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id          BIGINT       NOT NULL REFERENCES cliente_perfil(id) ON DELETE CASCADE,
    reserva_id          BIGINT       NOT NULL UNIQUE REFERENCES reserva(id) ON DELETE CASCADE,
    visita_numero       INT          NOT NULL,
    es_beneficio        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_fidelizacion_visita CHECK (visita_numero > 0)
);

CREATE INDEX idx_fidelizacion_cliente ON fidelizacion (cliente_id, visita_numero);


-- ─── Plan de Pagos de Eventos (agregado 2026-06-23) ───────────────────────────
--
-- Extiende la tabla evento con modalidad de pago (al contado o por cuotas)
-- y agrega la tabla evento_cuota para registrar el cronograma de pagos.
-- La primera cuota siempre corresponde al adelanto pagado al confirmar;
-- las cuotas restantes quedan PENDIENTE hasta que el admin las registre.

ALTER TABLE evento
    ADD COLUMN modalidad_pago    TEXT NOT NULL DEFAULT 'AL_CONTADO',
    ADD COLUMN fecha_limite_pago DATE;

ALTER TABLE evento
    ADD CONSTRAINT ck_evento_modalidad_pago
        CHECK (modalidad_pago IN ('AL_CONTADO', 'CUOTAS'));


CREATE TABLE evento_cuota (
    id                  BIGINT        GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id           BIGINT        NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    numero_cuota        INT           NOT NULL,
    monto               NUMERIC(10,2) NOT NULL,
    fecha_vencimiento   DATE          NOT NULL,
    estado              TEXT          NOT NULL DEFAULT 'PENDIENTE',
    venta_id            BIGINT        REFERENCES venta(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_cuota_numero  CHECK (numero_cuota > 0),
    CONSTRAINT ck_cuota_monto   CHECK (monto > 0),
    CONSTRAINT ck_cuota_estado  CHECK (estado IN ('PENDIENTE', 'PAGADO', 'VENCIDO')),
    CONSTRAINT uk_evento_cuota  UNIQUE (evento_id, numero_cuota)
);

CREATE INDEX idx_evento_cuota_evento      ON evento_cuota (evento_id);
CREATE INDEX idx_evento_cuota_vencimiento ON evento_cuota (fecha_vencimiento)
    WHERE estado = 'PENDIENTE';