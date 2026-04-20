-- V8: Inventario de productos

CREATE TABLE producto (
    idproducto         BIGSERIAL        PRIMARY KEY,
    idcategoria        INT           NOT NULL REFERENCES categoriaproducto(idcategoriaproducto),
    idsede             INT           NOT NULL REFERENCES sede(idsede),
    nombre             VARCHAR(150)  NOT NULL,
    descripcion        VARCHAR(400),
    precio             NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    stockactual        INT           NOT NULL DEFAULT 0 CHECK (stockactual >= 0),
    stockminimo        INT           NOT NULL DEFAULT 0 CHECK (stockminimo >= 0),
    unidadmedida       VARCHAR(40)   NOT NULL DEFAULT 'unidad',
    activo             BOOLEAN       NOT NULL DEFAULT TRUE,
    fechacreacion      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_producto_categoria ON producto(idcategoria);
CREATE INDEX idx_producto_sede      ON producto(idsede);
CREATE INDEX idx_producto_activo    ON producto(activo);

CREATE TABLE movimientoinventario (
    idmovimiento    BIGSERIAL       PRIMARY KEY,
    idproducto      INT          NOT NULL REFERENCES producto(idproducto),
    tipomovimiento  VARCHAR(20)  NOT NULL
                        CHECK (tipomovimiento IN ('ENTRADA','SALIDA','AJUSTE','BAJA')),
    cantidad        INT          NOT NULL,
    stockanterior   INT          NOT NULL,
    stockresultante INT          NOT NULL,
    motivo          VARCHAR(200) NOT NULL,
    idventa         INT          REFERENCES venta(idventa),
    idusuario       INT          REFERENCES usuarioadmin(idusuarioadmin),
    fechamovimiento TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimiento_producto ON movimientoinventario(idproducto);
CREATE INDEX idx_movimiento_fecha    ON movimientoinventario(fechamovimiento);

ALTER TABLE detalleventa
    ADD CONSTRAINT fk_detalle_producto
    FOREIGN KEY (idproducto) REFERENCES producto(idproducto);

CREATE INDEX idx_detalle_producto ON detalleventa(idproducto);