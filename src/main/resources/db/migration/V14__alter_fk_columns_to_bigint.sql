-- V13: Normalizar columnas FK de INT a BIGINT para coincidir con entidades JPA (Long)

-- usuarioadmin
ALTER TABLE usuarioadmin ALTER COLUMN idsede TYPE BIGINT;

-- feriado
ALTER TABLE feriado ALTER COLUMN creadopor TYPE BIGINT;

-- bloquecalendario
ALTER TABLE bloquecalendario ALTER COLUMN idsede            TYPE BIGINT;
ALTER TABLE bloquecalendario ALTER COLUMN idusuariocreador  TYPE BIGINT;

-- disponibilidaddiaria
ALTER TABLE disponibilidaddiaria ALTER COLUMN idsede TYPE BIGINT;

-- tarifa
ALTER TABLE tarifa ALTER COLUMN idsede           TYPE BIGINT;
ALTER TABLE tarifa ALTER COLUMN idusuariocreador TYPE BIGINT;

-- reservapublica
ALTER TABLE reservapublica ALTER COLUMN idcliente         TYPE BIGINT;
ALTER TABLE reservapublica ALTER COLUMN idsede            TYPE BIGINT;
ALTER TABLE reservapublica ALTER COLUMN idreservaoriginal TYPE BIGINT;

-- eventoprivado
ALTER TABLE eventoprivado ALTER COLUMN idcliente       TYPE BIGINT;
ALTER TABLE eventoprivado ALTER COLUMN idsede          TYPE BIGINT;
ALTER TABLE eventoprivado ALTER COLUMN idturno         TYPE BIGINT;
ALTER TABLE eventoprivado ALTER COLUMN idusuariogestor TYPE BIGINT;

-- contrato
ALTER TABLE contrato ALTER COLUMN ideventoprivado   TYPE BIGINT;
ALTER TABLE contrato ALTER COLUMN idusuarioredactor TYPE BIGINT;

-- contratoproveedor
ALTER TABLE contratoproveedor ALTER COLUMN idcontrato  TYPE BIGINT;
ALTER TABLE contratoproveedor ALTER COLUMN idproveedor TYPE BIGINT;

-- venta
ALTER TABLE venta ALTER COLUMN idsede           TYPE BIGINT;
ALTER TABLE venta ALTER COLUMN idusuario        TYPE BIGINT;
ALTER TABLE venta ALTER COLUMN idreservapublica TYPE BIGINT;
ALTER TABLE venta ALTER COLUMN ideventoprivado  TYPE BIGINT;

-- detalleventa
ALTER TABLE detalleventa ALTER COLUMN idventa    TYPE BIGINT;
ALTER TABLE detalleventa ALTER COLUMN idproducto TYPE BIGINT;

-- pago
ALTER TABLE pago ALTER COLUMN idreservapublica  TYPE BIGINT;
ALTER TABLE pago ALTER COLUMN ideventoprivado   TYPE BIGINT;
ALTER TABLE pago ALTER COLUMN idventa           TYPE BIGINT;
ALTER TABLE pago ALTER COLUMN idusuarioregistra TYPE BIGINT;

-- seriecomprobante
ALTER TABLE seriecomprobante ALTER COLUMN idsede TYPE BIGINT;

-- comprobante
ALTER TABLE comprobante ALTER COLUMN idpago          TYPE BIGINT;
ALTER TABLE comprobante ALTER COLUMN idserie         TYPE BIGINT;
ALTER TABLE comprobante ALTER COLUMN idcomprobantenta TYPE BIGINT;

-- producto
ALTER TABLE producto ALTER COLUMN idcategoria TYPE BIGINT;
ALTER TABLE producto ALTER COLUMN idsede      TYPE BIGINT;

-- movimientoinventario
ALTER TABLE movimientoinventario ALTER COLUMN idproducto TYPE BIGINT;
ALTER TABLE movimientoinventario ALTER COLUMN idventa    TYPE BIGINT;
ALTER TABLE movimientoinventario ALTER COLUMN idusuario  TYPE BIGINT;

-- promocion
ALTER TABLE promocion ALTER COLUMN idsede           TYPE BIGINT;
ALTER TABLE promocion ALTER COLUMN idusuariocreador TYPE BIGINT;

-- reservapublicapromocion
ALTER TABLE reservapublicapromocion ALTER COLUMN idreservapublica TYPE BIGINT;
ALTER TABLE reservapublicapromocion ALTER COLUMN idpromocion      TYPE BIGINT;

-- historialfidelizacion
ALTER TABLE historialfidelizacion ALTER COLUMN idcliente        TYPE BIGINT;
ALTER TABLE historialfidelizacion ALTER COLUMN idreservapublica TYPE BIGINT;

-- imagengaleria
ALTER TABLE imagengaleria ALTER COLUMN idsede         TYPE BIGINT;
ALTER TABLE imagengaleria ALTER COLUMN idusuarisubio  TYPE BIGINT;

-- banner
ALTER TABLE banner ALTER COLUMN idsede           TYPE BIGINT;
ALTER TABLE banner ALTER COLUMN idusuariocreador TYPE BIGINT;

-- resena
ALTER TABLE resena ALTER COLUMN idcliente        TYPE BIGINT;
ALTER TABLE resena ALTER COLUMN idusuarioaprueba TYPE BIGINT;

-- logauditoria
ALTER TABLE logauditoria ALTER COLUMN idusuarioadmin TYPE BIGINT;
ALTER TABLE logauditoria ALTER COLUMN identidad      TYPE BIGINT;
