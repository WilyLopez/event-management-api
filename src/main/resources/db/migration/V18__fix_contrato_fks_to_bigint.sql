-- V18: Corregir tipos de columnas FK en actividadcontrato y documentocontrato de INT a BIGINT

-- actividadcontrato
ALTER TABLE actividadcontrato ALTER COLUMN idcontrato TYPE BIGINT;
ALTER TABLE actividadcontrato ALTER COLUMN idusuario   TYPE BIGINT;

-- documentocontrato
ALTER TABLE documentocontrato ALTER COLUMN idcontrato     TYPE BIGINT;
ALTER TABLE documentocontrato ALTER COLUMN idusuariocarga TYPE BIGINT;
