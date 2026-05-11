-- V21: Correcciones estructurales del esquema

-- 1. Completar catálogo estadocontrato
--    V1 solo insertó BORRADOR y FIRMADO; el enum Java define 7 estados.
--    Sin estos registros, cualquier INSERT con otro estado viola la FK.
INSERT INTO estadocontrato (codigo, descripcion) VALUES
    ('ENVIADO',         'Contrato enviado al cliente para revisión'),
    ('PENDIENTE_FIRMA', 'Contrato pendiente de firma'),
    ('VENCIDO',         'Contrato expirado sin firma'),
    ('CANCELADO',       'Contrato cancelado'),
    ('ARCHIVADO',       'Contrato archivado')
ON CONFLICT (codigo) DO NOTHING;

-- 2. Renombrar logauditoria."timestamp" → fechalog
--    "timestamp" es palabra reservada SQL — debe ir entre comillas dobles
--    al referenciarla como identificador en DDL.
ALTER TABLE logauditoria RENAME COLUMN "timestamp" TO fechalog;

DROP INDEX IF EXISTS idx_log_timestamp;
CREATE INDEX idx_log_fechalog ON logauditoria(fechalog DESC);

-- 3. Corregir typo: imagengaleria.idusuarisubio → idusuariosubio
--    El typo (falta la 'o' en "usuario") viene desde V10 y fue propagado
--    en V14 (ALTER COLUMN TYPE BIGINT).
ALTER TABLE imagengaleria RENAME COLUMN idusuarisubio TO idusuariosubio;

-- 4. Corregir typo: sesionadmin.ultimactividad → ultimaactividad
--    El campo fue creado en V19 con la 'a' de "ultima" faltante.
ALTER TABLE sesionadmin RENAME COLUMN ultimactividad TO ultimaactividad;

-- 5. Agregar FK faltante: reservapublica.mediopago → mediopago(codigo)
--    V20 agregó el campo como VARCHAR(30) libre sin constraint referencial.
--    Se agrega como NOT VALID para no bloquear la migración si hubiera datos
--    previos inconsistentes; se valida explícitamente después.
ALTER TABLE reservapublica
    ADD CONSTRAINT fk_reserva_mediopago
    FOREIGN KEY (mediopago) REFERENCES mediopago(codigo) NOT VALID;

ALTER TABLE reservapublica VALIDATE CONSTRAINT fk_reserva_mediopago;

-- 6. Agregar CHECK constraint a eventoprivado.estadooperativo
--    V20 lo añadió como VARCHAR libre; controlar los valores operativos válidos.
--    NOT VALID para no bloquear si hay datos previos fuera del rango.
ALTER TABLE eventoprivado
    ADD CONSTRAINT ck_estadooperativo
    CHECK (estadooperativo IS NULL OR estadooperativo IN (
        'PENDIENTE_LOGISTICA', 'EN_PREPARACION', 'LISTO', 'EN_CURSO', 'FINALIZADO'
    )) NOT VALID;

ALTER TABLE eventoprivado VALIDATE CONSTRAINT ck_estadooperativo;

-- 7. Índice para búsqueda de sesiones por JWT token (faltante en V19)
CREATE INDEX IF NOT EXISTS idx_sesionadmin_token
    ON sesionadmin(tokenjti) WHERE tokenjti IS NOT NULL;
