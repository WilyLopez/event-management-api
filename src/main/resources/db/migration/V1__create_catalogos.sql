-- V1: Tablas de catálogo (enumeraciones controladas)

CREATE TABLE estadoreservapublica (
    idestadoreservapublica BIGSERIAL PRIMARY KEY,
    codigo                 VARCHAR(40)  NOT NULL UNIQUE,
    descripcion            VARCHAR(100) NOT NULL
);
INSERT INTO estadoreservapublica (codigo, descripcion) VALUES
    ('PENDIENTE',    'Reserva creada, pago aún no confirmado'),
    ('CONFIRMADA',   'Pago confirmado, ticket generado'),
    ('REPROGRAMADA', 'Entrada reprogramada a otra fecha'),
    ('COMPLETADA',   'Visita realizada'),
    ('CANCELADA',    'Reserva cancelada');

CREATE TABLE estadoeventoprivado (
    idestadoeventoprivado BIGSERIAL PRIMARY KEY,
    codigo                VARCHAR(40)  NOT NULL UNIQUE,
    descripcion           VARCHAR(100) NOT NULL
);
INSERT INTO estadoeventoprivado (codigo, descripcion) VALUES
    ('SOLICITADA', 'Solicitud recibida, pendiente de contacto'),
    ('CONFIRMADA', 'Contrato firmado, evento agendado'),
    ('COMPLETADA', 'Evento realizado exitosamente'),
    ('CANCELADA',  'Evento cancelado con justificación');

CREATE TABLE estadocontrato (
    idestadocontrato BIGSERIAL PRIMARY KEY,
    codigo           VARCHAR(40)  NOT NULL UNIQUE,
    descripcion      VARCHAR(100) NOT NULL
);
INSERT INTO estadocontrato (codigo, descripcion) VALUES
    ('BORRADOR', 'Contrato en redacción, no firmado'),
    ('FIRMADO',  'Contrato firmado por ambas partes');

CREATE TABLE tipocomprobante (
    idtipocomprobante BIGSERIAL PRIMARY KEY,
    codigo            VARCHAR(30)  NOT NULL UNIQUE,
    descripcion       VARCHAR(100) NOT NULL,
    validosunat       BOOLEAN      NOT NULL DEFAULT TRUE
);
INSERT INTO tipocomprobante (codigo, descripcion, validosunat) VALUES
    ('BOLETA',     'Boleta de Venta Electrónica',   TRUE),
    ('FACTURA',    'Factura Electrónica',            TRUE),
    ('NOTA_VENTA', 'Nota de Venta (uso interno)',    FALSE);

CREATE TABLE estadocomprobante (
    idestadocomprobante BIGSERIAL PRIMARY KEY,
    codigo              VARCHAR(40)  NOT NULL UNIQUE,
    descripcion         VARCHAR(100) NOT NULL
);
INSERT INTO estadocomprobante (codigo, descripcion) VALUES
    ('PENDIENTE',  'Pendiente de envío a SUNAT'),
    ('EMITIDO',    'Validado y aceptado por SUNAT'),
    ('RECHAZADO',  'Rechazado por SUNAT, requiere corrección'),
    ('ANULADO',    'Anulado mediante nota de crédito');

CREATE TABLE mediopago (
    idmediopago BIGSERIAL PRIMARY KEY,
    codigo      VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(80) NOT NULL,
    activo      BOOLEAN     NOT NULL DEFAULT TRUE
);
INSERT INTO mediopago (codigo, descripcion) VALUES
    ('YAPE',          'Pago vía Yape'),
    ('EFECTIVO',      'Pago en efectivo'),
    ('TRANSFERENCIA', 'Transferencia bancaria'),
    ('TARJETA',       'Pago con tarjeta (uso futuro)');

CREATE TABLE tipodia (
    idtipodiacod VARCHAR(30) PRIMARY KEY,
    descripcion  VARCHAR(80) NOT NULL
);
INSERT INTO tipodia (idtipodiacod, descripcion) VALUES
    ('SEMANA',             'Lunes a viernes (tarifa A)'),
    ('FIN_SEMANA_FERIADO', 'Sábado, domingo y feriados (tarifa B)');

CREATE TABLE canalreserva (
    idcanalreserva VARCHAR(30) PRIMARY KEY,
    descripcion     VARCHAR(80) NOT NULL
);
INSERT INTO canalreserva (idcanalreserva, descripcion) VALUES
    ('ONLINE',      'Reserva realizada desde el sitio web'),
    ('PRESENCIAL',  'Registro realizado en el local');

CREATE TABLE turno (
    idturno     BIGSERIAL PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    descripcion VARCHAR(60) NOT NULL,
    horainicio  TIME        NOT NULL,
    horafin     TIME        NOT NULL
);
INSERT INTO turno (codigo, descripcion, horainicio, horafin) VALUES
    ('T1', 'Turno mañana', '10:00', '14:00'),
    ('T2', 'Turno tarde',  '16:00', '20:00');

CREATE TABLE tipoferiado (
    idtipoferiado VARCHAR(20) PRIMARY KEY,
    descripcion   VARCHAR(80) NOT NULL
);
INSERT INTO tipoferiado (idtipoferiado, descripcion) VALUES
    ('NACIONAL', 'Feriado oficial nacional del Perú'),
    ('REGIONAL', 'Feriado regional o local');

CREATE TABLE tipopromocion (
    idtipopromocion VARCHAR(40) PRIMARY KEY,
    descripcion     VARCHAR(120) NOT NULL
);
INSERT INTO tipopromocion (idtipopromocion, descripcion) VALUES
    ('DESCUENTO_PORCENTAJE', 'Descuento porcentual sobre el precio base'),
    ('DESCUENTO_MONTO_FIJO', 'Descuento de monto fijo sobre el precio base'),
    ('PAQUETE_GRUPAL',       'Precio especial para grupos'),
    ('ENTRADA_GRATUITA',     'Entrada sin costo (fidelización)'),
    ('CLIENTE_FRECUENTE',    'Beneficio acumulativo por visitas');

CREATE TABLE categoriaproducto (
    idcategoriaproducto BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(80)  NOT NULL UNIQUE,
    descripcion         VARCHAR(200),
    activo              BOOLEAN      NOT NULL DEFAULT TRUE
);