-- V7: Ventas, pagos y movimientos de inventario
-- movimientoinventario se incluye aquí porque depende de venta (creada en esta misma migración).

-- ─── Ventas (cabecera) ────────────────────────────────────────────────────────
CREATE TABLE venta (
    idventa          BIGSERIAL     PRIMARY KEY,
    idsede           BIGINT        NOT NULL REFERENCES sede(idsede),
    idusuario        BIGINT        NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    idreservapublica BIGINT        REFERENCES reservapublica(idreservapublica),
    ideventoprivado  BIGINT        REFERENCES eventoprivado(ideventoprivado),
    subtotal         NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    descuento        NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (descuento >= 0),
    total            NUMERIC(10,2) NOT NULL CHECK (total >= 0),
    fechaventa       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_venta_total CHECK (total = subtotal - descuento)
);

CREATE INDEX idx_venta_sede_fecha ON venta(idsede, fechaventa);
CREATE INDEX idx_venta_usuario    ON venta(idusuario);

-- ─── Líneas de detalle de cada venta ─────────────────────────────────────────
CREATE TABLE detalleventa (
    iddetalleventa BIGSERIAL     PRIMARY KEY,
    idventa        BIGINT        NOT NULL REFERENCES venta(idventa),
    idproducto     BIGINT        NOT NULL REFERENCES producto(idproducto),
    cantidad       INT           NOT NULL CHECK (cantidad > 0),
    preciounitario NUMERIC(10,2) NOT NULL CHECK (preciounitario >= 0),
    subtotallinea  NUMERIC(10,2) NOT NULL CHECK (subtotallinea >= 0),
    CONSTRAINT ck_subtotal_linea CHECK (subtotallinea = cantidad * preciounitario)
);

CREATE INDEX idx_detalle_venta    ON detalleventa(idventa);
CREATE INDEX idx_detalle_producto ON detalleventa(idproducto);

-- ─── Pagos (asociados a reserva, evento o venta) ──────────────────────────────
CREATE TABLE pago (
    idpago            BIGSERIAL     PRIMARY KEY,
    idmediopago       VARCHAR(30)   NOT NULL REFERENCES mediopago(codigo),
    idreservapublica  BIGINT        REFERENCES reservapublica(idreservapublica),
    ideventoprivado   BIGINT        REFERENCES eventoprivado(ideventoprivado),
    idventa           BIGINT        REFERENCES venta(idventa),
    monto             NUMERIC(10,2) NOT NULL CHECK (monto > 0),
    referenciapago    VARCHAR(100),
    esparcial         BOOLEAN       NOT NULL DEFAULT FALSE,
    tipopago          VARCHAR(30)   NOT NULL DEFAULT 'UNICO'
                          CHECK (tipopago IN ('UNICO', 'ADELANTO', 'SALDO')),
    idusuarioregistra BIGINT        REFERENCES usuarioadmin(idusuarioadmin),
    fechapago         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_pago_contexto_unico CHECK (
        (CASE WHEN idreservapublica IS NOT NULL THEN 1 ELSE 0 END +
         CASE WHEN ideventoprivado  IS NOT NULL THEN 1 ELSE 0 END +
         CASE WHEN idventa          IS NOT NULL THEN 1 ELSE 0 END) = 1
    )
);

CREATE INDEX idx_pago_reserva ON pago(idreservapublica) WHERE idreservapublica IS NOT NULL;
CREATE INDEX idx_pago_evento  ON pago(ideventoprivado)  WHERE ideventoprivado  IS NOT NULL;
CREATE INDEX idx_pago_venta   ON pago(idventa)          WHERE idventa           IS NOT NULL;
CREATE INDEX idx_pago_fecha   ON pago(fechapago);

-- ─── Movimientos de inventario ────────────────────────────────────────────────
CREATE TABLE movimientoinventario (
    idmovimiento    BIGSERIAL    PRIMARY KEY,
    idproducto      BIGINT       NOT NULL REFERENCES producto(idproducto),
    tipomovimiento  VARCHAR(20)  NOT NULL
                        CHECK (tipomovimiento IN ('ENTRADA', 'SALIDA', 'AJUSTE', 'BAJA')),
    cantidad        INT          NOT NULL,
    stockanterior   INT          NOT NULL,
    stockresultante INT          NOT NULL,
    motivo          VARCHAR(200) NOT NULL,
    idventa         BIGINT       REFERENCES venta(idventa),
    idusuario       BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechamovimiento TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimiento_producto ON movimientoinventario(idproducto);
CREATE INDEX idx_movimiento_fecha    ON movimientoinventario(fechamovimiento);
