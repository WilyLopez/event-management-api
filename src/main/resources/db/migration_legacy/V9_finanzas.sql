CREATE TABLE apertura_caja (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
    fecha               DATE         NOT NULL,
    estado_codigo       TEXT         NOT NULL DEFAULT 'ABIERTA' REFERENCES estado_caja(codigo) ON UPDATE CASCADE,
    saldo_inicial       NUMERIC(10,2) NOT NULL DEFAULT 0,
    saldo_final         NUMERIC(10,2),
    total_ingresos      NUMERIC(10,2) NOT NULL DEFAULT 0,
    total_egresos       NUMERIC(10,2) NOT NULL DEFAULT 0,
    diferencia          NUMERIC(10,2),
    apertura_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    apertura_por        UUID         NOT NULL REFERENCES perfil_usuario(id),
    cierre_at           TIMESTAMPTZ,
    cierre_por          UUID         REFERENCES perfil_usuario(id),
    observaciones       TEXT,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_apertura_caja_sede_fecha UNIQUE (sede_id, fecha),
    CONSTRAINT ck_apertura_saldo_inicial   CHECK (saldo_inicial >= 0),
    CONSTRAINT ck_apertura_saldo_final     CHECK (saldo_final IS NULL OR saldo_final >= 0),
    CONSTRAINT ck_apertura_totales         CHECK (total_ingresos >= 0 AND total_egresos >= 0),
    CONSTRAINT ck_apertura_cierre          CHECK (
        (estado_codigo = 'ABIERTA' AND cierre_at IS NULL)
        OR (estado_codigo = 'CERRADA' AND cierre_at IS NOT NULL)
    )
);

CREATE INDEX idx_apertura_caja_sede_fecha ON apertura_caja (sede_id, fecha);
CREATE INDEX idx_apertura_caja_abierta    ON apertura_caja (sede_id) WHERE estado_codigo = 'ABIERTA';

CREATE TRIGGER trg_apertura_caja_updated_at
    BEFORE UPDATE ON apertura_caja
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE registro_ingreso (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_codigo         TEXT         NOT NULL REFERENCES tipo_ingreso(codigo) ON UPDATE CASCADE,
    sede_id             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
    venta_id            BIGINT       REFERENCES venta(id) ON DELETE SET NULL,
    venta_pago_id       BIGINT       REFERENCES venta_pago(id) ON DELETE SET NULL,
    reserva_id          BIGINT       REFERENCES reserva(id) ON DELETE SET NULL,
    evento_id           BIGINT       REFERENCES evento(id) ON DELETE SET NULL,

    monto               NUMERIC(10,2) NOT NULL,
    fecha               DATE         NOT NULL,
    medio_pago_codigo   TEXT         REFERENCES medio_pago(codigo) ON UPDATE CASCADE,
    referencia          TEXT,
    descripcion         TEXT,
    es_automatico       BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          UUID         REFERENCES perfil_usuario(id),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_registro_ingreso_monto CHECK (monto > 0)
);

CREATE INDEX idx_registro_ingreso_sede_fecha ON registro_ingreso (sede_id, fecha) WHERE deleted_at IS NULL;
CREATE INDEX idx_registro_ingreso_tipo       ON registro_ingreso (tipo_codigo);
CREATE INDEX idx_registro_ingreso_medio      ON registro_ingreso (medio_pago_codigo) WHERE medio_pago_codigo IS NOT NULL;
CREATE INDEX idx_registro_ingreso_venta      ON registro_ingreso (venta_id)          WHERE venta_id IS NOT NULL;
CREATE INDEX idx_registro_ingreso_reserva    ON registro_ingreso (reserva_id)        WHERE reserva_id IS NOT NULL;
CREATE INDEX idx_registro_ingreso_evento     ON registro_ingreso (evento_id)         WHERE evento_id IS NOT NULL;

CREATE TRIGGER trg_registro_ingreso_updated_at
    BEFORE UPDATE ON registro_ingreso
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE registro_egreso (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_codigo         TEXT         NOT NULL REFERENCES tipo_egreso(codigo) ON UPDATE CASCADE,
    sede_id             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,

    monto               NUMERIC(10,2) NOT NULL,
    fecha               DATE         NOT NULL,
    periodo_anio        INT,
    periodo_mes         INT,
    medio_pago_codigo   TEXT         REFERENCES medio_pago(codigo) ON UPDATE CASCADE,
    descripcion         TEXT,
    comprobante_path    TEXT,
    es_recurrente       BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          UUID         NOT NULL REFERENCES perfil_usuario(id),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_registro_egreso_monto  CHECK (monto > 0),
    CONSTRAINT ck_registro_egreso_mes    CHECK (periodo_mes  IS NULL OR periodo_mes  BETWEEN 1 AND 12),
    CONSTRAINT ck_registro_egreso_anio   CHECK (periodo_anio IS NULL OR periodo_anio BETWEEN 2020 AND 2100)
);

CREATE INDEX idx_registro_egreso_sede_fecha ON registro_egreso (sede_id, fecha) WHERE deleted_at IS NULL;
CREATE INDEX idx_registro_egreso_tipo       ON registro_egreso (tipo_codigo);
CREATE INDEX idx_registro_egreso_periodo    ON registro_egreso (periodo_anio, periodo_mes);

CREATE TRIGGER trg_registro_egreso_updated_at
    BEFORE UPDATE ON registro_egreso
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE movimiento_caja (
    id                      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    apertura_caja_id        BIGINT       NOT NULL REFERENCES apertura_caja(id) ON DELETE RESTRICT,
    tipo                    TEXT         NOT NULL,
    concepto                TEXT         NOT NULL,
    monto                   NUMERIC(10,2) NOT NULL,
    medio_pago_codigo       TEXT         REFERENCES medio_pago(codigo) ON UPDATE CASCADE,
    registro_ingreso_id     BIGINT       REFERENCES registro_ingreso(id) ON DELETE SET NULL,
    registro_egreso_id      BIGINT       REFERENCES registro_egreso(id)  ON DELETE SET NULL,
    venta_id                BIGINT       REFERENCES venta(id) ON DELETE SET NULL,
    es_manual               BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by              UUID         NOT NULL REFERENCES perfil_usuario(id),

    CONSTRAINT ck_movimiento_caja_tipo  CHECK (tipo IN ('INGRESO','EGRESO')),
    CONSTRAINT ck_movimiento_caja_monto CHECK (monto > 0)
);

CREATE INDEX idx_movimiento_caja_apertura ON movimiento_caja (apertura_caja_id, created_at);
CREATE INDEX idx_movimiento_caja_tipo     ON movimiento_caja (tipo);


CREATE TABLE presupuesto_evento (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id           BIGINT       NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    concepto            TEXT         NOT NULL,
    categoria           TEXT,
    monto_estimado      NUMERIC(10,2) NOT NULL DEFAULT 0,
    monto_real          NUMERIC(10,2),
    estado              TEXT         NOT NULL DEFAULT 'PENDIENTE',
    orden               INT          NOT NULL DEFAULT 0,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          UUID         REFERENCES perfil_usuario(id),
    updated_by          UUID         REFERENCES perfil_usuario(id),

    CONSTRAINT ck_presupuesto_estimado CHECK (monto_estimado >= 0),
    CONSTRAINT ck_presupuesto_real     CHECK (monto_real IS NULL OR monto_real >= 0),
    CONSTRAINT ck_presupuesto_estado   CHECK (estado IN ('PENDIENTE','APROBADO','EJECUTADO','DESCARTADO'))
);

CREATE INDEX idx_presupuesto_evento_evento ON presupuesto_evento (evento_id, orden);

CREATE TRIGGER trg_presupuesto_evento_updated_at
    BEFORE UPDATE ON presupuesto_evento
    FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();


CREATE TABLE gasto_evento (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id           BIGINT       NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    descripcion         TEXT         NOT NULL,
    monto               NUMERIC(10,2) NOT NULL,
    comprobante_path    TEXT,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          UUID         NOT NULL REFERENCES perfil_usuario(id),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_gasto_evento_monto CHECK (monto > 0)
);

CREATE INDEX idx_gasto_evento_evento ON gasto_evento (evento_id) WHERE deleted_at IS NULL;


CREATE TABLE gasto_operativo_diario (
    id                  BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sede_id             BIGINT       NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
    fecha               DATE         NOT NULL,
    descripcion         TEXT         NOT NULL,
    monto               NUMERIC(10,2) NOT NULL,
    comprobante_path    TEXT,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          UUID         NOT NULL REFERENCES perfil_usuario(id),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT ck_gasto_operativo_monto CHECK (monto > 0)
);

CREATE INDEX idx_gasto_operativo_sede_fecha ON gasto_operativo_diario (sede_id, fecha) WHERE deleted_at IS NULL;