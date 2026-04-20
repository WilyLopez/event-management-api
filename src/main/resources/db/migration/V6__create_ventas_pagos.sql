-- V6: Ventas y pagos

CREATE TABLE venta (
    idventa          BIGSERIAL        PRIMARY KEY,
    idsede           INT           NOT NULL REFERENCES sede(idsede),
    idusuario        INT           NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    idreservapublica INT           REFERENCES reservapublica(idreservapublica),
    ideventoprivado  INT           REFERENCES eventoprivado(ideventoprivado),
    subtotal         NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    descuento        NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (descuento >= 0),
    total            NUMERIC(10,2) NOT NULL CHECK (total >= 0),
    fechaventa       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_venta_total CHECK (total = subtotal - descuento)
);

CREATE INDEX idx_venta_sede_fecha ON venta(idsede, fechaventa);
CREATE INDEX idx_venta_usuario    ON venta(idusuario);

CREATE TABLE detalleventa (
    iddetalleventa BIGSERIAL        PRIMARY KEY,
    idventa        INT           NOT NULL REFERENCES venta(idventa),
    idproducto     INT           NOT NULL,
    cantidad       INT           NOT NULL CHECK (cantidad > 0),
    preciounitario NUMERIC(10,2) NOT NULL CHECK (preciounitario >= 0),
    subtotallinea  NUMERIC(10,2) NOT NULL CHECK (subtotallinea >= 0),
    CONSTRAINT ck_subtotal_linea CHECK (subtotallinea = cantidad * preciounitario)
);

CREATE INDEX idx_detalle_venta ON detalleventa(idventa);

CREATE TABLE pago (
    idpago            BIGSERIAL        PRIMARY KEY,
    idmediopago       VARCHAR(30)   NOT NULL REFERENCES mediopago(codigo),
    idreservapublica  INT           REFERENCES reservapublica(idreservapublica),
    ideventoprivado   INT           REFERENCES eventoprivado(ideventoprivado),
    idventa           INT           REFERENCES venta(idventa),
    monto             NUMERIC(10,2) NOT NULL CHECK (monto > 0),
    referenciapago    VARCHAR(100),
    esparcial         BOOLEAN       NOT NULL DEFAULT FALSE,
    tipopago          VARCHAR(30)   NOT NULL DEFAULT 'UNICO'
                          CHECK (tipopago IN ('UNICO','ADELANTO','SALDO')),
    idusuarioregistra INT           REFERENCES usuarioadmin(idusuarioadmin),
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