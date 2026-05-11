-- V6: Reservas públicas, eventos privados, checklist, proveedores y contratos

-- ─── Reservas públicas (acceso general) ──────────────────────────────────────
CREATE TABLE reservapublica (
    idreservapublica    BIGSERIAL     PRIMARY KEY,
    idcliente           BIGINT        NOT NULL REFERENCES cliente(idcliente),
    idsede              BIGINT        NOT NULL REFERENCES sede(idsede),
    idestado            VARCHAR(40)   NOT NULL REFERENCES estadoreservapublica(codigo),
    idcanalreserva      VARCHAR(30)   NOT NULL REFERENCES canalreserva(idcanalreserva),
    idtipodiacod        VARCHAR(30)   NOT NULL REFERENCES tipodia(idtipodiacod),
    idreservaoriginal   BIGINT        REFERENCES reservapublica(idreservapublica),
    esreprogramacion    BOOLEAN       NOT NULL DEFAULT FALSE,
    vecesreprogramada   INT           NOT NULL DEFAULT 0 CHECK (vecesreprogramada >= 0),
    fechaevento         DATE          NOT NULL,
    numeroticket        VARCHAR(50)   NOT NULL UNIQUE,
    preciohistorico     NUMERIC(10,2) NOT NULL CHECK (preciohistorico >= 0),
    descuentoaplicado   NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (descuentoaplicado >= 0),
    totalpagado         NUMERIC(10,2) NOT NULL CHECK (totalpagado >= 0),
    nombrenino          VARCHAR(120)  NOT NULL,
    edadnino            INT           NOT NULL CHECK (edadnino BETWEEN 0 AND 17),
    nombreacompanante   VARCHAR(120)  NOT NULL,
    dniacompanante      VARCHAR(8)    NOT NULL,
    firmoconsentimiento BOOLEAN       NOT NULL DEFAULT FALSE,
    motivocancelacion   VARCHAR(300),
    ingresado           BOOLEAN       NOT NULL DEFAULT FALSE,
    fechaingreso        TIMESTAMPTZ,
    codigoqr            VARCHAR(200),
    mediopago           VARCHAR(30)   REFERENCES mediopago(codigo),
    referenciapago      VARCHAR(100),
    fechacreacion       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    fechaactualizacion  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_reprog_tiene_original CHECK (esreprogramacion = FALSE OR idreservaoriginal IS NOT NULL),
    CONSTRAINT ck_total_coherente       CHECK (totalpagado = preciohistorico - descuentoaplicado)
);

CREATE INDEX idx_reservapub_cliente    ON reservapublica(idcliente);
CREATE INDEX idx_reservapub_fecha      ON reservapublica(fechaevento);
CREATE INDEX idx_reservapub_estado     ON reservapublica(idestado);
CREATE INDEX idx_reservapub_ticket     ON reservapublica(numeroticket);
CREATE INDEX idx_reservapub_sede_fecha ON reservapublica(idsede, fechaevento);
CREATE INDEX idx_reservapub_original   ON reservapublica(idreservaoriginal) WHERE idreservaoriginal IS NOT NULL;
CREATE INDEX idx_reservapub_ingresado  ON reservapublica(ingresado) WHERE ingresado = TRUE;

-- ─── Eventos privados (celebraciones, cumpleaños, etc.) ───────────────────────
CREATE TABLE eventoprivado (
    ideventoprivado     BIGSERIAL     PRIMARY KEY,
    idcliente           BIGINT        NOT NULL REFERENCES cliente(idcliente),
    idsede              BIGINT        NOT NULL REFERENCES sede(idsede),
    idestado            VARCHAR(40)   NOT NULL REFERENCES estadoeventoprivado(codigo),
    idturno             BIGINT        NOT NULL REFERENCES turno(idturno),
    fechaevento         DATE          NOT NULL,
    tipoevento          VARCHAR(200)  NOT NULL,
    contactoadicional   VARCHAR(200),
    aforodeclarado      INT           CHECK (aforodeclarado BETWEEN 1 AND 60),
    preciototalcontrato NUMERIC(10,2) CHECK (preciototalcontrato >= 0),
    montoadelanto       NUMERIC(10,2) DEFAULT 0 CHECK (montoadelanto >= 0),
    motivocancelacion   VARCHAR(500),
    notasinternas       TEXT,
    idusuariogestor     BIGINT        REFERENCES usuarioadmin(idusuarioadmin),
    estadooperativo     VARCHAR(40)   CHECK (estadooperativo IS NULL OR estadooperativo IN (
                            'PENDIENTE_LOGISTICA', 'EN_PREPARACION', 'LISTO', 'EN_CURSO', 'FINALIZADO'
                        )),
    checklistcompleto   BOOLEAN       NOT NULL DEFAULT FALSE,
    horainicioreal      TIMESTAMPTZ,
    horafinreal         TIMESTAMPTZ,
    fechacreacion       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    fechaactualizacion  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (idsede, fechaevento, idturno)
);

CREATE INDEX idx_eventopri_cliente    ON eventoprivado(idcliente);
CREATE INDEX idx_eventopri_sede_fecha ON eventoprivado(idsede, fechaevento);
CREATE INDEX idx_eventopri_estado     ON eventoprivado(idestado);

-- ─── Checklist operativo del evento privado ───────────────────────────────────
CREATE TABLE checklistevento (
    idchecklist       BIGSERIAL    PRIMARY KEY,
    ideventoprivado   BIGINT       NOT NULL REFERENCES eventoprivado(ideventoprivado) ON DELETE CASCADE,
    tarea             VARCHAR(200) NOT NULL,
    completada        BOOLEAN      NOT NULL DEFAULT FALSE,
    orden             INT          NOT NULL DEFAULT 0,
    idusuariocompleto BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechacompletado   TIMESTAMPTZ,
    fechacreacion     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_checklist_evento ON checklistevento(ideventoprivado);

-- ─── Proveedores de servicios externos ────────────────────────────────────────
CREATE TABLE proveedor (
    idproveedor        BIGSERIAL    PRIMARY KEY,
    nombre             VARCHAR(200) NOT NULL,
    ruc                VARCHAR(11)  UNIQUE,
    contactonombre     VARCHAR(120),
    contactotelefono   VARCHAR(20),
    contactocorreo     VARCHAR(120),
    tiposervicio       VARCHAR(200) NOT NULL,
    notas              TEXT,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ─── Contrato del evento privado ──────────────────────────────────────────────
CREATE TABLE contrato (
    idcontrato         BIGSERIAL    PRIMARY KEY,
    ideventoprivado    BIGINT       NOT NULL UNIQUE REFERENCES eventoprivado(ideventoprivado),
    idestado           VARCHAR(40)  NOT NULL REFERENCES estadocontrato(codigo),
    contenidotexto     TEXT         NOT NULL,
    archivopdfurl      VARCHAR(500),
    fechafirma         DATE,
    version            INT          NOT NULL DEFAULT 1,
    plantilla          VARCHAR(60),
    observaciones      TEXT,
    idusuarioredactor  BIGINT       NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacreacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fechaactualizacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ─── Proveedores asociados al contrato ────────────────────────────────────────
CREATE TABLE contratoproveedor (
    idcontratoproveedor BIGSERIAL     PRIMARY KEY,
    idcontrato          BIGINT        NOT NULL REFERENCES contrato(idcontrato),
    idproveedor         BIGINT        NOT NULL REFERENCES proveedor(idproveedor),
    serviciodescripcion VARCHAR(300),
    montoacordado       NUMERIC(10,2) CHECK (montoacordado >= 0),
    contratadopor       VARCHAR(30)   NOT NULL DEFAULT 'EMPRESA'
                            CHECK (contratadopor IN ('EMPRESA', 'CLIENTE')),
    UNIQUE (idcontrato, idproveedor)
);

-- ─── Documentos adjuntos al contrato ──────────────────────────────────────────
CREATE TABLE documentocontrato (
    iddocumento    BIGSERIAL    PRIMARY KEY,
    idcontrato     BIGINT       NOT NULL REFERENCES contrato(idcontrato) ON DELETE CASCADE,
    nombre         VARCHAR(300) NOT NULL,
    archivourl     VARCHAR(500) NOT NULL,
    tipoarchivo    VARCHAR(50)  NOT NULL,
    tamanobytes    BIGINT,
    idusuariocarga BIGINT       NOT NULL REFERENCES usuarioadmin(idusuarioadmin),
    fechacarga     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_doccontrato_contrato ON documentocontrato(idcontrato);

-- ─── Historial de actividad del contrato ──────────────────────────────────────
CREATE TABLE actividadcontrato (
    idactividad BIGSERIAL    PRIMARY KEY,
    idcontrato  BIGINT       NOT NULL REFERENCES contrato(idcontrato) ON DELETE CASCADE,
    accion      VARCHAR(80)  NOT NULL,
    descripcion VARCHAR(400),
    idusuario   BIGINT       REFERENCES usuarioadmin(idusuarioadmin),
    fechaaccion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_actividadcontrato_contrato ON actividadcontrato(idcontrato);
