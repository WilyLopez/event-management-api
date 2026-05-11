-- V5: Inventario de productos
-- Definido antes de ventas para que detalleventa pueda referenciar producto directamente.

CREATE TABLE producto (
    idproducto         BIGSERIAL     PRIMARY KEY,
    idcategoria        BIGINT        NOT NULL REFERENCES categoriaproducto(idcategoriaproducto),
    idsede             BIGINT        NOT NULL REFERENCES sede(idsede),
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
