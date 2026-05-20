CREATE TABLE promocion (
    idpromocion               BIGSERIAL     PRIMARY KEY,
    idtipopromocion           VARCHAR(40)   NOT NULL REFERENCES tipopromocion(idtipopromocion),
    idsede                    BIGINT        REFERENCES sede(idsede),
    nombre                    VARCHAR(150)  NOT NULL,
    descripcion               VARCHAR(400),
    valordescuento            NUMERIC(10,2) NOT NULL CHECK (valordescuento >= 0),
    condicion                 VARCHAR(300),
    minimopersonas            INT,
    solotipodiacod            VARCHAR(30)   REFERENCES tipodia(idtipodiacod),
    fechainicio               DATE          NOT NULL,
    fechafin                  DATE,
    activo                    BOOLEAN       NOT NULL DEFAULT TRUE,
    esautomatica              BOOLEAN       NOT NULL DEFAULT TRUE,
    idusuariocreador          BIGINT        NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion             TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    imagenurl                 VARCHAR(500),
    bannerurl                 VARCHAR(500),
    colordestacado            VARCHAR(20),
    prioridad                 INTEGER       NOT NULL DEFAULT 0,
    textopublicitario         VARCHAR(300),
    textoboton                VARCHAR(100),
    urlboton                  VARCHAR(500),
    mostrarennicio            BOOLEAN       NOT NULL DEFAULT FALSE,
    mostrarencarrusel         BOOLEAN       NOT NULL DEFAULT FALSE,
    mostrareenpaginapromociones BOOLEAN     NOT NULL DEFAULT TRUE,
    mostarencheckout          BOOLEAN       NOT NULL DEFAULT FALSE,
    mostrardestacado          BOOLEAN       NOT NULL DEFAULT FALSE,
    solomovil                 BOOLEAN       NOT NULL DEFAULT FALSE,
    limiteusos                INTEGER,
    limitepocliente           INTEGER,
    minimoasistentes          INTEGER,
    montominimo               NUMERIC(10,2),
    CONSTRAINT ck_promo_fechas CHECK (fechafin IS NULL OR fechafin >= fechainicio)
);

CREATE INDEX idx_promo_activo_fechas  ON promocion(activo, fechainicio, fechafin);
CREATE INDEX idx_promo_tipo_dia       ON promocion(solotipodiacod) WHERE solotipodiacod IS NOT NULL;
CREATE INDEX idx_promo_mostrarinicio  ON promocion(mostrarennicio) WHERE mostrarennicio = TRUE;
CREATE INDEX idx_promo_prioridad      ON promocion(prioridad DESC);

CREATE TABLE reservapublicapromocion (
    idreservapublicapromocion BIGSERIAL     PRIMARY KEY,
    idreservapublica          BIGINT        NOT NULL REFERENCES reservapublica(idreservapublica),
    idpromocion               BIGINT        NOT NULL REFERENCES promocion(idpromocion),
    montodescuentoaplicado    NUMERIC(10,2) NOT NULL CHECK (montodescuentoaplicado >= 0),
    UNIQUE (idreservapublica, idpromocion)
);

CREATE TABLE historialfidelizacion (
    idhistorialfidelizacion BIGSERIAL   PRIMARY KEY,
    idcliente               BIGINT      NOT NULL REFERENCES cliente(idcliente),
    idreservapublica        BIGINT      NOT NULL UNIQUE REFERENCES reservapublica(idreservapublica),
    visitanumero            INT         NOT NULL CHECK (visitanumero > 0),
    esbeneficioaplicado     BOOLEAN     NOT NULL DEFAULT FALSE,
    fecharegistro           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fideliz_cliente ON historialfidelizacion(idcliente);
CREATE INDEX idx_fideliz_reserva ON historialfidelizacion(idreservapublica);
