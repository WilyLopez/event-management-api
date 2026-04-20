-- V8: Promociones y fidelización

CREATE TABLE promocion (
    idpromocion      BIGSERIAL        PRIMARY KEY,
    idtipopromocion  VARCHAR(40)   NOT NULL REFERENCES tipopromocion(idtipopromocion),
    idsede           INT           REFERENCES sede(idsede),
    nombre           VARCHAR(150)  NOT NULL,
    descripcion      VARCHAR(400),
    valordescuento   NUMERIC(10,2) NOT NULL CHECK (valordescuento >= 0),
    condicion        VARCHAR(300),
    minimopersonas   INT,
    solotipodiacod   VARCHAR(30)   REFERENCES tipodia(idtipodiacod),
    fechainicio      DATE          NOT NULL,
    fechafin         DATE,
    activo           BOOLEAN       NOT NULL DEFAULT TRUE,
    esautomatica     BOOLEAN       NOT NULL DEFAULT TRUE,
    idusuariocreador INT           NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_promo_fechas CHECK (fechafin IS NULL OR fechafin >= fechainicio)
);

CREATE INDEX idx_promo_activo_fechas ON promocion(activo, fechainicio, fechafin);
CREATE INDEX idx_promo_tipo_dia      ON promocion(solotipodiacod) WHERE solotipodiacod IS NOT NULL;

CREATE TABLE reservapublicapromocion (
    idreservapublicapromocion BIGSERIAL        PRIMARY KEY,
    idreservapublica          INT           NOT NULL REFERENCES reservapublica(idreservapublica),
    idpromocion               INT           NOT NULL REFERENCES promocion(idpromocion),
    montodescuentoaplicado    NUMERIC(10,2) NOT NULL CHECK (montodescuentoaplicado >= 0),
    UNIQUE (idreservapublica, idpromocion)
);

CREATE TABLE historialfidelizacion (
    idhistorialfidelizacion BIGSERIAL      PRIMARY KEY,
    idcliente               INT         NOT NULL REFERENCES cliente(idcliente),
    idreservapublica        INT         NOT NULL UNIQUE REFERENCES reservapublica(idreservapublica),
    visitanumero            INT    NOT NULL CHECK (visitanumero > 0),
    esbeneficioaplicado     BOOLEAN     NOT NULL DEFAULT FALSE,
    fecharegistro           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fideliz_cliente ON historialfidelizacion(idcliente);
CREATE INDEX idx_fideliz_reserva ON historialfidelizacion(idreservapublica);