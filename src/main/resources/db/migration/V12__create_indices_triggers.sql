-- V11: Funciones y triggers de soporte

CREATE OR REPLACE FUNCTION fn_actualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fechaactualizacion = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cliente_timestamp
    BEFORE UPDATE ON cliente FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_usuarioadmin_timestamp
    BEFORE UPDATE ON usuarioadmin FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_eventoprivado_timestamp
    BEFORE UPDATE ON eventoprivado FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_contrato_timestamp
    BEFORE UPDATE ON contrato FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_proveedor_timestamp
    BEFORE UPDATE ON proveedor FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_producto_timestamp
    BEFORE UPDATE ON producto FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_reservapublica_timestamp
    BEFORE UPDATE ON reservapublica FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE TRIGGER trg_comprobante_timestamp
    BEFORE UPDATE ON comprobante FOR EACH ROW EXECUTE FUNCTION fn_actualizar_timestamp();

CREATE OR REPLACE FUNCTION fn_actualizar_aforo_diario()
RETURNS TRIGGER AS $$
DECLARE
    v_codigoNuevo    TEXT;
    v_codigoAnterior TEXT;
BEGIN
    SELECT codigo INTO v_codigoNuevo    FROM estadoreservapublica WHERE codigo = NEW.idestado;
    SELECT codigo INTO v_codigoAnterior FROM estadoreservapublica WHERE codigo = OLD.idestado;

    INSERT INTO disponibilidaddiaria (idsede, fecha)
    VALUES (NEW.idsede, NEW.fechaevento)
    ON CONFLICT (idsede, fecha) DO NOTHING;

    IF v_codigoNuevo = 'CONFIRMADA' AND v_codigoAnterior != 'CONFIRMADA' THEN
        UPDATE disponibilidaddiaria
        SET aforopublicoactual = aforopublicoactual + 1, fechaactualizacion = NOW()
        WHERE idsede = NEW.idsede AND fecha = NEW.fechaevento;
    END IF;

    IF v_codigoAnterior = 'CONFIRMADA' AND v_codigoNuevo != 'CONFIRMADA' THEN
        UPDATE disponibilidaddiaria
        SET aforopublicoactual = GREATEST(0, aforopublicoactual - 1), fechaactualizacion = NOW()
        WHERE idsede = NEW.idsede AND fecha = NEW.fechaevento;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reservapub_aforo
AFTER UPDATE ON reservapublica FOR EACH ROW
WHEN (OLD.idestado IS DISTINCT FROM NEW.idestado)
EXECUTE FUNCTION fn_actualizar_aforo_diario();

CREATE OR REPLACE FUNCTION fn_bloquear_disponibilidad_evento_privado()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.idestado = 'CONFIRMADA' THEN
        INSERT INTO disponibilidaddiaria (idsede, fecha, accesopublicoactivo)
        VALUES (NEW.idsede, NEW.fechaevento, FALSE)
        ON CONFLICT (idsede, fecha)
        DO UPDATE SET accesopublicoactivo = FALSE, fechaactualizacion = NOW();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_evento_privado_disponibilidad
AFTER INSERT OR UPDATE ON eventoprivado FOR EACH ROW
EXECUTE FUNCTION fn_bloquear_disponibilidad_evento_privado();

CREATE OR REPLACE FUNCTION fn_sincronizar_stock()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE producto
    SET stockactual = NEW.stockresultante, fechaactualizacion = NOW()
    WHERE idproducto = NEW.idproducto;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_movimiento_stock
AFTER INSERT ON movimientoinventario FOR EACH ROW
EXECUTE FUNCTION fn_sincronizar_stock();