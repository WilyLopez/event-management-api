-- =====================================================================
-- LIMPIEZA DE MOVIMIENTOS DE CAJA DUPLICADOS (one-time, ejecutar manual)
-- =====================================================================
-- Contexto: VentaMostradorService insertaba un movimiento_caja por cada pago
-- ADEMAS del que ya generan los triggers de BD
-- (trg_venta_pago_registrar_ingreso -> trg_registro_ingreso_movimiento_caja).
-- Resultado: por cada venta en efectivo quedaban 2 movimientos y la apertura
-- quedaba con total_ingresos inflado.
--
-- Los movimientos creados por la app se distinguen porque NO tienen
-- registro_ingreso_id (los de trigger SI lo tienen), son es_manual = FALSE y
-- estan ligados a una venta. Los movimientos de Yape/Plin/Tarjeta creados por la
-- app tampoco deben existir en caja (la caja solo refleja efectivo), por lo que
-- se eliminan todos los de este origen.
--
-- EJECUTAR DENTRO DE UNA TRANSACCION Y REVISAR EL PASO 1 ANTES DE BORRAR.
-- =====================================================================

-- ---------------------------------------------------------------------
-- PASO 1 (DIAGNOSTICO): ver cuantos movimientos se eliminarian por apertura.
-- Ejecutar primero de forma aislada y revisar los numeros.
-- ---------------------------------------------------------------------
SELECT
    m.apertura_caja_id,
    COUNT(*)               AS movimientos_app,
    SUM(m.monto)           AS monto_total_app
FROM movimiento_caja m
WHERE m.es_manual = FALSE
  AND m.registro_ingreso_id IS NULL
  AND m.registro_egreso_id IS NULL
  AND m.venta_id IS NOT NULL
  AND m.tipo = 'INGRESO'
GROUP BY m.apertura_caja_id
ORDER BY m.apertura_caja_id;

-- ---------------------------------------------------------------------
-- PASO 2 (CORRECCION): eliminar duplicados y recalcular totales.
-- Descomentar el bloque y ejecutar una vez revisado el PASO 1.
-- ---------------------------------------------------------------------
-- BEGIN;
--
-- -- 2.1 Eliminar los movimientos creados por la aplicacion (duplicados).
-- DELETE FROM movimiento_caja m
-- WHERE m.es_manual = FALSE
--   AND m.registro_ingreso_id IS NULL
--   AND m.registro_egreso_id IS NULL
--   AND m.venta_id IS NOT NULL
--   AND m.tipo = 'INGRESO';
--
-- -- 2.2 Recalcular total_ingresos / total_egresos de las cajas ABIERTAS a partir
-- --     de los movimientos restantes (los de trigger + los manuales). Las cajas
-- --     CERRADAS NO se tocan para no alterar arqueos/cierres historicos; revisar
-- --     esos casos manualmente si fuese necesario.
-- UPDATE apertura_caja a
-- SET total_ingresos = COALESCE((
--         SELECT SUM(m.monto) FROM movimiento_caja m
--         WHERE m.apertura_caja_id = a.id AND m.tipo = 'INGRESO'
--     ), 0),
--     total_egresos = COALESCE((
--         SELECT SUM(m.monto) FROM movimiento_caja m
--         WHERE m.apertura_caja_id = a.id AND m.tipo = 'EGRESO'
--     ), 0),
--     updated_at = NOW()
-- WHERE a.estado_codigo = 'ABIERTA';
--
-- -- 2.3 Verificar cajas CERRADAS afectadas (revision manual).
-- --     Si esta consulta devuelve filas, esas cajas cerradas tenian duplicados
-- --     y su total_ingresos quedo inflado respecto a sus movimientos reales.
-- --     SELECT a.id, a.fecha, a.total_ingresos,
-- --            COALESCE((SELECT SUM(m.monto) FROM movimiento_caja m
-- --                      WHERE m.apertura_caja_id = a.id AND m.tipo='INGRESO'),0) AS ingresos_reales
-- --     FROM apertura_caja a
-- --     WHERE a.estado_codigo = 'CERRADA'
-- --       AND a.total_ingresos <> COALESCE((SELECT SUM(m.monto) FROM movimiento_caja m
-- --                      WHERE m.apertura_caja_id = a.id AND m.tipo='INGRESO'),0);
--
-- COMMIT;
