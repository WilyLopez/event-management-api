-- Corrección masiva de tipos INT a BIGINT para claves foráneas que referencian BIGSERIAL
-- Esto soluciona errores de validación de Hibernate 6 (Schema-validation: wrong column type encountered)

-- Referencias a sede(idsede)
ALTER TABLE usuarioadmin ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE bloquecalendario ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE disponibilidaddiaria ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE tarifa ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE reservapublica ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE eventoprivado ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE venta ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE seriecomprobante ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE producto ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE promocion ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE imagengaleria ALTER COLUMN idsede TYPE BIGINT;
ALTER TABLE banner ALTER COLUMN idsede TYPE BIGINT;

-- Referencias a usuarioadmin(idusuarioadmin)
ALTER TABLE feriado ALTER COLUMN creadopor TYPE BIGINT;
ALTER TABLE bloquecalendario ALTER COLUMN creadopor TYPE BIGINT;
ALTER TABLE eventoprivado ALTER COLUMN idusuariogestor TYPE BIGINT;
ALTER TABLE venta ALTER COLUMN idusuarioregistra TYPE BIGINT;
ALTER TABLE movimientoinventario ALTER COLUMN idusuario TYPE BIGINT;
ALTER TABLE contenidoweb ALTER COLUMN idusuarioeditor TYPE BIGINT;
ALTER TABLE imagengaleria ALTER COLUMN idusuarisubio TYPE BIGINT;
ALTER TABLE banner ALTER COLUMN idusuariocreador TYPE BIGINT;
ALTER TABLE resena ALTER COLUMN idusuarioaprueba TYPE BIGINT;
ALTER TABLE log_auditoria ALTER COLUMN idusuarioadmin TYPE BIGINT;

-- Referencias a reservapublica(idreservapublica) / eventoprivado(ideventoprivado)
ALTER TABLE reservapublica ALTER COLUMN idreservaoriginal TYPE BIGINT;
ALTER TABLE venta ALTER COLUMN idreservapublica TYPE BIGINT;
ALTER TABLE venta ALTER COLUMN ideventoprivado TYPE BIGINT;
ALTER TABLE pago ALTER COLUMN idreservapublica TYPE BIGINT;
ALTER TABLE pago ALTER COLUMN ideventoprivado TYPE BIGINT;

-- Referencias a venta(idventa)
ALTER TABLE detalleventa ALTER COLUMN idventa TYPE BIGINT;
ALTER TABLE pago ALTER COLUMN idventa TYPE BIGINT;
ALTER TABLE movimientoinventario ALTER COLUMN idventa TYPE BIGINT;

-- Referencias a cliente(idcliente)
ALTER TABLE resena ALTER COLUMN idcliente TYPE BIGINT;

-- Otras referencias comunes
ALTER TABLE comprobante ALTER COLUMN idserie TYPE BIGINT;
ALTER TABLE comprobante ALTER COLUMN idpago TYPE BIGINT;
ALTER TABLE comprobante ALTER COLUMN idcomprobantenta TYPE BIGINT;
ALTER TABLE facturacion ALTER COLUMN idcomprobante TYPE BIGINT;
