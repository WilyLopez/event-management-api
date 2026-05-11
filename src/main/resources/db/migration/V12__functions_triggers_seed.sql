-- V12: Funciones, triggers automáticos y datos iniciales del sistema

-- ═══════════════════════════════════════════════════════════════════════════════
-- FUNCIONES Y TRIGGERS
-- ═══════════════════════════════════════════════════════════════════════════════

-- ─── Actualización automática de fechaactualizacion ──────────────────────────
CREATE OR REPLACE FUNCTION fn_actualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fechaactualizacion = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_usuarioadmin_timestamp
    BEFORE UPDATE ON usuarioadmin
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_cliente_timestamp
    BEFORE UPDATE ON cliente
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_eventoprivado_timestamp
    BEFORE UPDATE ON eventoprivado
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_contrato_timestamp
    BEFORE UPDATE ON contrato
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_proveedor_timestamp
    BEFORE UPDATE ON proveedor
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_producto_timestamp
    BEFORE UPDATE ON producto
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_reservapublica_timestamp
    BEFORE UPDATE ON reservapublica
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_comprobante_timestamp
    BEFORE UPDATE ON comprobante
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

-- ─── Sincronización del aforo público diario ─────────────────────────────────
-- Se ejecuta cuando cambia el estado de una reserva pública.
-- Incrementa el aforo al confirmar; lo decrementa al desconfirmar.
CREATE OR REPLACE FUNCTION fn_actualizar_aforo_diario()
RETURNS TRIGGER AS $$
BEGIN
    -- Garantiza que exista el registro de disponibilidad para esa fecha
    INSERT INTO disponibilidaddiaria (idsede, fecha)
    VALUES (NEW.idsede, NEW.fechaevento)
    ON CONFLICT (idsede, fecha) DO NOTHING;

    IF NEW.idestado = 'CONFIRMADA' AND OLD.idestado <> 'CONFIRMADA' THEN
        UPDATE disponibilidaddiaria
        SET aforopublicoactual = aforopublicoactual + 1,
            fechaactualizacion = NOW()
        WHERE idsede = NEW.idsede AND fecha = NEW.fechaevento;
    END IF;

    IF OLD.idestado = 'CONFIRMADA' AND NEW.idestado <> 'CONFIRMADA' THEN
        UPDATE disponibilidaddiaria
        SET aforopublicoactual = GREATEST(0, aforopublicoactual - 1),
            fechaactualizacion = NOW()
        WHERE idsede = NEW.idsede AND fecha = NEW.fechaevento;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reservapub_aforo
    AFTER UPDATE ON reservapublica
    FOR EACH ROW
    WHEN (OLD.idestado IS DISTINCT FROM NEW.idestado)
    EXECUTE FUNCTION fn_actualizar_aforo_diario();

-- ─── Bloqueo de disponibilidad al confirmar un evento privado ─────────────────
-- Al confirmar un evento privado, marca el día como no disponible para el público.
CREATE OR REPLACE FUNCTION fn_bloquear_disponibilidad_evento_privado()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.idestado = 'CONFIRMADA' THEN
        INSERT INTO disponibilidaddiaria (idsede, fecha, accesopublicoactivo)
        VALUES (NEW.idsede, NEW.fechaevento, FALSE)
        ON CONFLICT (idsede, fecha)
        DO UPDATE SET accesopublicoactivo = FALSE,
                      fechaactualizacion  = NOW();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_evento_privado_disponibilidad
    AFTER INSERT OR UPDATE ON eventoprivado
    FOR EACH ROW
    EXECUTE FUNCTION fn_bloquear_disponibilidad_evento_privado();

-- ─── Sincronización de stock tras movimiento de inventario ────────────────────
-- Actualiza producto.stockactual con el valor resultante de cada movimiento.
CREATE OR REPLACE FUNCTION fn_sincronizar_stock()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE producto
    SET stockactual        = NEW.stockresultante,
        fechaactualizacion = NOW()
    WHERE idproducto = NEW.idproducto;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_movimiento_stock
    AFTER INSERT ON movimientoinventario
    FOR EACH ROW
    EXECUTE FUNCTION fn_sincronizar_stock();

-- ═══════════════════════════════════════════════════════════════════════════════
-- DATOS INICIALES
-- ═══════════════════════════════════════════════════════════════════════════════

-- ─── Usuario administrador por defecto ───────────────────────────────────────
-- Contraseña por defecto: admin123 (hash BCrypt 10 rounds)
-- IMPORTANTE: cambiar la contraseña tras el primer inicio de sesión.
INSERT INTO usuarioadmin (
    idsede,
    nombre,
    correo,
    contresenahash,
    rol,
    debecambiarcontrasena
) VALUES (
    1,
    'Administrador Sistema',
    'admin@playzone.com',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7uSyLnS',
    'SUPERADMIN',
    TRUE
);

-- ─── Tarifas base de la sede principal ───────────────────────────────────────
INSERT INTO tarifa (idsede, idtipodiacod, precio, vigenciadesde, idusuariocreador)
VALUES
    (1, 'SEMANA',             25.00, '2024-01-01', 1),
    (1, 'FIN_SEMANA_FERIADO', 35.00, '2024-01-01', 1);
