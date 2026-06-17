CREATE OR REPLACE FUNCTION app.tipo_dia_de_fecha(p_sede_id BIGINT, p_fecha DATE)
RETURNS TEXT
LANGUAGE sql
STABLE
AS $$
    SELECT CASE
        WHEN EXTRACT(ISODOW FROM p_fecha) IN (6, 7) THEN 'FIN_SEMANA_FERIADO'
        WHEN EXISTS (
            SELECT 1 FROM feriado f
            WHERE f.fecha = p_fecha
              AND (f.sede_id = p_sede_id OR f.sede_id IS NULL)
              AND f.deleted_at IS NULL
        ) THEN 'FIN_SEMANA_FERIADO'
        ELSE 'SEMANA'
    END;
$$;


CREATE OR REPLACE FUNCTION app.precio_vigente(p_sede_id BIGINT, p_fecha DATE)
RETURNS NUMERIC(10,2)
LANGUAGE sql
STABLE
AS $$
    SELECT t.precio
    FROM tarifa t
    WHERE t.sede_id = p_sede_id
      AND t.tipo_dia_codigo = app.tipo_dia_de_fecha(p_sede_id, p_fecha)
      AND t.es_activo = TRUE
      AND t.deleted_at IS NULL
      AND t.vigencia_desde <= p_fecha
      AND (t.vigencia_hasta IS NULL OR t.vigencia_hasta >= p_fecha)
    ORDER BY t.vigencia_desde DESC
    LIMIT 1;
$$;


CREATE OR REPLACE FUNCTION app.aforo_ocupado(p_sede_id BIGINT, p_fecha DATE)
RETURNS INT
LANGUAGE sql
STABLE
AS $$
    SELECT COUNT(*)::INT
    FROM reserva r
    WHERE r.sede_id = p_sede_id
      AND r.fecha_evento = p_fecha
      AND r.estado_codigo IN ('PENDIENTE', 'CONFIRMADA', 'REPROGRAMADA')
      AND r.deleted_at IS NULL;
$$;


CREATE OR REPLACE FUNCTION app.dia_tiene_evento_privado(p_sede_id BIGINT, p_fecha DATE)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
AS $$
    SELECT EXISTS (
        SELECT 1 FROM evento e
        WHERE e.sede_id = p_sede_id
          AND e.fecha_evento = p_fecha
          AND e.estado_codigo IN ('SOLICITADA', 'CONFIRMADA', 'EN_CURSO')
          AND e.deleted_at IS NULL
    );
$$;


CREATE OR REPLACE FUNCTION app.dia_tiene_reserva_publica(p_sede_id BIGINT, p_fecha DATE)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
AS $$
    SELECT EXISTS (
        SELECT 1 FROM reserva r
        WHERE r.sede_id = p_sede_id
          AND r.fecha_evento = p_fecha
          AND r.estado_codigo IN ('PENDIENTE', 'CONFIRMADA', 'REPROGRAMADA')
          AND r.deleted_at IS NULL
    );
$$;


CREATE OR REPLACE FUNCTION app.turno_ocupado(p_sede_id BIGINT, p_fecha DATE, p_turno_codigo TEXT)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
AS $$
    SELECT EXISTS (
        SELECT 1 FROM evento e
        WHERE e.sede_id = p_sede_id
          AND e.fecha_evento = p_fecha
          AND e.turno_codigo = p_turno_codigo
          AND e.estado_codigo IN ('SOLICITADA', 'CONFIRMADA', 'EN_CURSO')
          AND e.deleted_at IS NULL
    );
$$;


CREATE OR REPLACE FUNCTION app.dia_esta_bloqueado(p_sede_id BIGINT, p_fecha DATE, p_turno_codigo TEXT DEFAULT NULL)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
AS $$
    SELECT EXISTS (
        SELECT 1 FROM bloque_calendario b
        WHERE b.sede_id = p_sede_id
          AND p_fecha BETWEEN b.fecha_inicio AND b.fecha_fin
          AND b.es_activo = TRUE
          AND b.deleted_at IS NULL
          AND (b.turno_codigo IS NULL OR b.turno_codigo = p_turno_codigo OR p_turno_codigo IS NULL)
    );
$$;


CREATE OR REPLACE FUNCTION app.es_feriado(p_sede_id BIGINT, p_fecha DATE)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
AS $$
    SELECT EXISTS (
        SELECT 1 FROM feriado f
        WHERE f.fecha = p_fecha
          AND (f.sede_id = p_sede_id OR f.sede_id IS NULL)
          AND f.deleted_at IS NULL
    );
$$;


CREATE OR REPLACE FUNCTION app.validar_reserva_publica()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cfg          configuracion_sede%ROWTYPE;
    v_aforo_actual INT;
    v_hoy          DATE := app.hoy();
    v_dias_anticip INT;
BEGIN
    SELECT * INTO v_cfg FROM configuracion_sede WHERE sede_id = NEW.sede_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No hay configuracion de calendario para la sede %', NEW.sede_id;
    END IF;

    IF NEW.fecha_evento < v_hoy THEN
        RAISE EXCEPTION 'No se puede reservar en fechas pasadas (fecha: %)', NEW.fecha_evento
            USING ERRCODE = 'check_violation';
    END IF;

    v_dias_anticip := NEW.fecha_evento - v_hoy;
    IF v_dias_anticip > v_cfg.dias_max_reserva_publica THEN
        RAISE EXCEPTION 'La fecha excede el limite de % dias de anticipacion', v_cfg.dias_max_reserva_publica
            USING ERRCODE = 'check_violation';
    END IF;

    IF NEW.fecha_evento = v_hoy AND (app.ahora() AT TIME ZONE 'America/Lima')::TIME > v_cfg.hora_cierre THEN
        RAISE EXCEPTION 'El local ya cerro por hoy. Hora actual supera %', v_cfg.hora_cierre
            USING ERRCODE = 'check_violation';
    END IF;

    IF app.dia_tiene_evento_privado(NEW.sede_id, NEW.fecha_evento) THEN
        RAISE EXCEPTION 'La fecha tiene un evento privado y no admite reservas publicas'
            USING ERRCODE = 'check_violation';
    END IF;

    IF app.dia_esta_bloqueado(NEW.sede_id, NEW.fecha_evento) THEN
        RAISE EXCEPTION 'La fecha esta bloqueada'
            USING ERRCODE = 'check_violation';
    END IF;

    IF app.es_feriado(NEW.sede_id, NEW.fecha_evento) THEN
        RAISE EXCEPTION 'La fecha es feriado'
            USING ERRCODE = 'check_violation';
    END IF;

    IF NEW.estado_codigo IN ('PENDIENTE', 'CONFIRMADA', 'REPROGRAMADA') THEN
        SELECT COUNT(*)::INT INTO v_aforo_actual
        FROM reserva r
        WHERE r.sede_id = NEW.sede_id
          AND r.fecha_evento = NEW.fecha_evento
          AND r.estado_codigo IN ('PENDIENTE', 'CONFIRMADA', 'REPROGRAMADA')
          AND r.deleted_at IS NULL
          AND r.id <> COALESCE(NEW.id, -1);

        IF v_aforo_actual + 1 > v_cfg.aforo_maximo THEN
            RAISE EXCEPTION 'Aforo maximo (%) alcanzado para la fecha %', v_cfg.aforo_maximo, NEW.fecha_evento
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;

    NEW.tipo_dia_codigo := COALESCE(NEW.tipo_dia_codigo, app.tipo_dia_de_fecha(NEW.sede_id, NEW.fecha_evento));

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_reserva_validar
    BEFORE INSERT OR UPDATE OF fecha_evento, sede_id, estado_codigo
    ON reserva
    FOR EACH ROW
    EXECUTE FUNCTION app.validar_reserva_publica();


CREATE OR REPLACE FUNCTION app.validar_evento_privado()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cfg          configuracion_sede%ROWTYPE;
    v_hoy          DATE := app.hoy();
    v_dias_anticip INT;
BEGIN
    SELECT * INTO v_cfg FROM configuracion_sede WHERE sede_id = NEW.sede_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No hay configuracion de calendario para la sede %', NEW.sede_id;
    END IF;

    IF NEW.fecha_evento < v_hoy THEN
        RAISE EXCEPTION 'No se puede crear evento en fechas pasadas (fecha: %)', NEW.fecha_evento
            USING ERRCODE = 'check_violation';
    END IF;

    v_dias_anticip := NEW.fecha_evento - v_hoy;

    IF NEW.estado_codigo = 'SOLICITADA' AND TG_OP = 'INSERT' THEN
        IF v_dias_anticip < v_cfg.dias_min_evento_privado THEN
            RAISE EXCEPTION 'Los eventos privados requieren al menos % dias de anticipacion (faltan % dias)',
                v_cfg.dias_min_evento_privado, v_dias_anticip
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;

    IF v_dias_anticip > v_cfg.dias_max_evento_privado THEN
        RAISE EXCEPTION 'La fecha excede el limite de % dias para eventos privados', v_cfg.dias_max_evento_privado
            USING ERRCODE = 'check_violation';
    END IF;

    IF NEW.estado_codigo IN ('SOLICITADA', 'CONFIRMADA', 'EN_CURSO') THEN
        IF app.dia_tiene_reserva_publica(NEW.sede_id, NEW.fecha_evento) THEN
            RAISE EXCEPTION 'La fecha ya tiene reservas publicas y no admite eventos privados'
                USING ERRCODE = 'check_violation';
        END IF;

        IF app.dia_esta_bloqueado(NEW.sede_id, NEW.fecha_evento, NEW.turno_codigo) THEN
            RAISE EXCEPTION 'La fecha o turno esta bloqueado'
                USING ERRCODE = 'check_violation';
        END IF;

        IF app.es_feriado(NEW.sede_id, NEW.fecha_evento) THEN
            RAISE EXCEPTION 'La fecha es feriado'
                USING ERRCODE = 'check_violation';
        END IF;

        IF EXISTS (
            SELECT 1 FROM evento e
            WHERE e.sede_id = NEW.sede_id
              AND e.fecha_evento = NEW.fecha_evento
              AND e.turno_codigo = NEW.turno_codigo
              AND e.estado_codigo IN ('SOLICITADA', 'CONFIRMADA', 'EN_CURSO')
              AND e.deleted_at IS NULL
              AND e.id <> COALESCE(NEW.id, -1)
        ) THEN
            RAISE EXCEPTION 'El turno % ya tiene un evento privado para la fecha %', NEW.turno_codigo, NEW.fecha_evento
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_evento_validar
    BEFORE INSERT OR UPDATE OF fecha_evento, sede_id, turno_codigo, estado_codigo
    ON evento
    FOR EACH ROW
    EXECUTE FUNCTION app.validar_evento_privado();


CREATE OR REPLACE FUNCTION app.generar_numero_ticket()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_correlativo BIGINT;
BEGIN
    IF NEW.numero_ticket IS NULL OR NEW.numero_ticket = '' THEN
        v_correlativo := nextval('seq_numero_ticket');
        NEW.numero_ticket := 'TKT-' || NEW.sede_id || '-' || to_char(NEW.fecha_evento, 'YYYYMMDD') || '-' || lpad(v_correlativo::TEXT, 6, '0');
    END IF;

    IF NEW.codigo_qr IS NULL OR NEW.codigo_qr = '' THEN
        NEW.codigo_qr := encode(gen_random_bytes(16), 'hex');
    END IF;

    RETURN NEW;
END;
$$;

CREATE SEQUENCE IF NOT EXISTS seq_numero_ticket;

CREATE TRIGGER trg_reserva_generar_ticket
    BEFORE INSERT ON reserva
    FOR EACH ROW
    EXECUTE FUNCTION app.generar_numero_ticket();


CREATE OR REPLACE FUNCTION app.actualizar_metricas_cliente()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cliente_id BIGINT;
BEGIN
    IF TG_OP = 'INSERT' THEN
        v_cliente_id := NEW.cliente_id;
    ELSIF TG_OP = 'UPDATE' THEN
        IF NEW.cliente_id IS DISTINCT FROM OLD.cliente_id
           OR NEW.estado_codigo IS DISTINCT FROM OLD.estado_codigo
           OR NEW.deleted_at IS DISTINCT FROM OLD.deleted_at
           OR NEW.total_pagado IS DISTINCT FROM OLD.total_pagado THEN
            v_cliente_id := COALESCE(NEW.cliente_id, OLD.cliente_id);
        ELSE
            RETURN NEW;
        END IF;
    ELSIF TG_OP = 'DELETE' THEN
        v_cliente_id := OLD.cliente_id;
    END IF;

    IF v_cliente_id IS NULL THEN
        RETURN COALESCE(NEW, OLD);
    END IF;

    UPDATE cliente_perfil cp
    SET
        contador_visitas = (
            SELECT COUNT(*)
            FROM reserva r
            WHERE r.cliente_id = v_cliente_id
              AND r.estado_codigo = 'COMPLETADA'
              AND r.deleted_at IS NULL
        ),
        total_gastado = (
            SELECT COALESCE(SUM(r.total_pagado), 0)
            FROM reserva r
            WHERE r.cliente_id = v_cliente_id
              AND r.estado_codigo IN ('CONFIRMADA', 'COMPLETADA', 'REPROGRAMADA')
              AND r.deleted_at IS NULL
        ),
        ultima_visita_at = (
            SELECT MAX(r.ingreso_at)
            FROM reserva r
            WHERE r.cliente_id = v_cliente_id
              AND r.ingresado = TRUE
              AND r.deleted_at IS NULL
        ),
        updated_at = NOW()
    WHERE cp.id = v_cliente_id;

    RETURN COALESCE(NEW, OLD);
END;
$$;

CREATE TRIGGER trg_reserva_metricas_cliente
    AFTER INSERT OR UPDATE OR DELETE
    ON reserva
    FOR EACH ROW
    EXECUTE FUNCTION app.actualizar_metricas_cliente();


CREATE OR REPLACE FUNCTION app.actualizar_segmento_cliente()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.es_vip = TRUE THEN
        NEW.segmento_codigo := 'VIP';
    ELSIF NEW.ruc IS NOT NULL THEN
        NEW.segmento_codigo := 'CORPORATIVO';
    ELSIF NEW.contador_visitas >= 6 THEN
        NEW.segmento_codigo := 'FRECUENTE';
    ELSIF NEW.ultima_visita_at IS NOT NULL
          AND NEW.ultima_visita_at < (NOW() - INTERVAL '6 months') THEN
        NEW.segmento_codigo := 'INACTIVO';
    ELSIF NEW.contador_visitas = 0 THEN
        NEW.segmento_codigo := 'NUEVO';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_cliente_actualizar_segmento
    BEFORE UPDATE OF contador_visitas, total_gastado, ultima_visita_at, es_vip, ruc
    ON cliente_perfil
    FOR EACH ROW
    EXECUTE FUNCTION app.actualizar_segmento_cliente();


CREATE OR REPLACE FUNCTION app.registrar_ingreso_desde_venta_pago()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_venta        venta%ROWTYPE;
    v_tipo_ingreso TEXT;
    v_reserva_id   BIGINT;
BEGIN
    IF NEW.es_validado = FALSE THEN
        RETURN NEW;
    END IF;

    SELECT * INTO v_venta FROM venta WHERE id = NEW.venta_id;
    IF NOT FOUND THEN
        RETURN NEW;
    END IF;

    v_tipo_ingreso := CASE v_venta.tipo
        WHEN 'RESERVA'         THEN 'RESERVA_PUBLICA'
        WHEN 'ADELANTO_EVENTO' THEN 'ADELANTO_EVENTO'
        WHEN 'SALDO_EVENTO'    THEN 'SALDO_EVENTO'
        ELSE 'OTRO'
    END;

    SELECT id INTO v_reserva_id FROM reserva WHERE venta_id = NEW.venta_id LIMIT 1;

    INSERT INTO registro_ingreso (
        tipo_codigo, sede_id, venta_id, venta_pago_id,
        reserva_id, evento_id, monto, fecha, medio_pago_codigo,
        referencia, descripcion, es_automatico, created_by
    ) VALUES (
        v_tipo_ingreso, v_venta.sede_id, NEW.venta_id, NEW.id,
        v_reserva_id, v_venta.evento_id, NEW.monto, COALESCE(v_venta.fecha_visita, CURRENT_DATE),
        NEW.medio_pago_codigo, NEW.referencia,
        'Ingreso automatico desde venta_pago #' || NEW.id, TRUE, v_venta.created_by
    );

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_venta_pago_registrar_ingreso
    AFTER INSERT ON venta_pago
    FOR EACH ROW
    EXECUTE FUNCTION app.registrar_ingreso_desde_venta_pago();


CREATE OR REPLACE FUNCTION app.registrar_movimiento_caja_desde_ingreso()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_apertura_id BIGINT;
    v_es_efectivo BOOLEAN;
BEGIN
    IF NEW.medio_pago_codigo IS NULL THEN
        RETURN NEW;
    END IF;

    SELECT es_efectivo INTO v_es_efectivo
    FROM medio_pago WHERE codigo = NEW.medio_pago_codigo;

    IF v_es_efectivo IS NOT TRUE THEN
        RETURN NEW;
    END IF;

    SELECT id INTO v_apertura_id
    FROM apertura_caja
    WHERE sede_id = NEW.sede_id
      AND fecha = NEW.fecha
      AND estado_codigo = 'ABIERTA'
    LIMIT 1;

    IF v_apertura_id IS NULL THEN
        RETURN NEW;
    END IF;

    INSERT INTO movimiento_caja (
        apertura_caja_id, tipo, concepto, monto, medio_pago_codigo,
        registro_ingreso_id, venta_id, es_manual, created_by
    ) VALUES (
        v_apertura_id, 'INGRESO',
        COALESCE(NEW.descripcion, 'Ingreso automatico'),
        NEW.monto, NEW.medio_pago_codigo,
        NEW.id, NEW.venta_id, FALSE, NEW.created_by
    );

    UPDATE apertura_caja
    SET total_ingresos = total_ingresos + NEW.monto,
        updated_at = NOW()
    WHERE id = v_apertura_id;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_registro_ingreso_movimiento_caja
    AFTER INSERT ON registro_ingreso
    FOR EACH ROW
    EXECUTE FUNCTION app.registrar_movimiento_caja_desde_ingreso();


CREATE OR REPLACE FUNCTION app.registrar_movimiento_caja_desde_egreso()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_apertura_id BIGINT;
    v_es_efectivo BOOLEAN;
BEGIN
    IF NEW.medio_pago_codigo IS NULL THEN
        RETURN NEW;
    END IF;

    SELECT es_efectivo INTO v_es_efectivo
    FROM medio_pago WHERE codigo = NEW.medio_pago_codigo;

    IF v_es_efectivo IS NOT TRUE THEN
        RETURN NEW;
    END IF;

    SELECT id INTO v_apertura_id
    FROM apertura_caja
    WHERE sede_id = NEW.sede_id
      AND fecha = NEW.fecha
      AND estado_codigo = 'ABIERTA'
    LIMIT 1;

    IF v_apertura_id IS NULL THEN
        RETURN NEW;
    END IF;

    INSERT INTO movimiento_caja (
        apertura_caja_id, tipo, concepto, monto, medio_pago_codigo,
        registro_egreso_id, es_manual, created_by
    ) VALUES (
        v_apertura_id, 'EGRESO',
        COALESCE(NEW.descripcion, 'Egreso automatico'),
        NEW.monto, NEW.medio_pago_codigo,
        NEW.id, FALSE, NEW.created_by
    );

    UPDATE apertura_caja
    SET total_egresos = total_egresos + NEW.monto,
        updated_at = NOW()
    WHERE id = v_apertura_id;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_registro_egreso_movimiento_caja
    AFTER INSERT ON registro_egreso
    FOR EACH ROW
    EXECUTE FUNCTION app.registrar_movimiento_caja_desde_egreso();


CREATE OR REPLACE FUNCTION app.validar_cierre_caja()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.estado_codigo = 'CERRADA' AND OLD.estado_codigo = 'ABIERTA' THEN
        NEW.cierre_at := COALESCE(NEW.cierre_at, NOW());
        NEW.cierre_por := COALESCE(NEW.cierre_por, auth.uid());

        IF NEW.saldo_final IS NULL THEN
            RAISE EXCEPTION 'Debe registrar el saldo final al cerrar caja'
                USING ERRCODE = 'check_violation';
        END IF;

        NEW.diferencia := NEW.saldo_final - (NEW.saldo_inicial + NEW.total_ingresos - NEW.total_egresos);
    END IF;

    IF NEW.estado_codigo = 'ABIERTA' AND OLD.estado_codigo = 'CERRADA' THEN
        RAISE EXCEPTION 'No se puede reabrir una caja cerrada'
            USING ERRCODE = 'check_violation';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_apertura_caja_validar_cierre
    BEFORE UPDATE OF estado_codigo, saldo_final
    ON apertura_caja
    FOR EACH ROW
    EXECUTE FUNCTION app.validar_cierre_caja();


CREATE OR REPLACE FUNCTION app.generar_numero_comprobante()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_serie       TEXT;
    v_correlativo BIGINT;
BEGIN
    IF NEW.correlativo IS NOT NULL AND NEW.numero_completo IS NOT NULL THEN
        RETURN NEW;
    END IF;

    SELECT s.serie INTO v_serie
    FROM serie_comprobante s
    WHERE s.id = NEW.serie_comprobante_id
      AND s.es_activa = TRUE
    FOR UPDATE;

    IF v_serie IS NULL THEN
        RAISE EXCEPTION 'Serie de comprobante no encontrada o inactiva: %', NEW.serie_comprobante_id;
    END IF;

    v_correlativo := app.obtener_siguiente_correlativo(
        (SELECT sede_id FROM serie_comprobante WHERE id = NEW.serie_comprobante_id),
        NEW.tipo_comp_codigo
    );

    NEW.serie           := v_serie;
    NEW.correlativo     := v_correlativo;
    NEW.numero_completo := v_serie || '-' || lpad(v_correlativo::TEXT, 8, '0');

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_comprobante_generar_numero
    BEFORE INSERT ON comprobante
    FOR EACH ROW
    EXECUTE FUNCTION app.generar_numero_comprobante();


CREATE OR REPLACE FUNCTION app.notificar_evento(
    p_tipo_codigo            TEXT,
    p_destinatario_usuario   UUID,
    p_destinatario_cliente   BIGINT,
    p_entidad_tipo           TEXT,
    p_entidad_id             BIGINT,
    p_titulo                 TEXT,
    p_mensaje                TEXT,
    p_url_accion             TEXT DEFAULT NULL,
    p_metadata               JSONB DEFAULT '{}'::jsonb
)
RETURNS BIGINT
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_notif_id BIGINT;
    v_tipo     tipo_notificacion%ROWTYPE;
    v_canal    TEXT;
BEGIN
    SELECT * INTO v_tipo FROM tipo_notificacion WHERE codigo = p_tipo_codigo AND activo = TRUE;
    IF NOT FOUND THEN
        RAISE NOTICE 'Tipo de notificacion no existe o inactivo: %', p_tipo_codigo;
        RETURN NULL;
    END IF;

    INSERT INTO notificacion (
        tipo_codigo, destinatario_usuario_id, destinatario_cliente_id,
        entidad_tipo, entidad_id, titulo, mensaje, url_accion, prioridad, metadata
    ) VALUES (
        p_tipo_codigo, p_destinatario_usuario, p_destinatario_cliente,
        p_entidad_tipo, p_entidad_id, p_titulo, p_mensaje, p_url_accion,
        v_tipo.prioridad, p_metadata
    )
    RETURNING id INTO v_notif_id;

    FOREACH v_canal IN ARRAY v_tipo.canales_default LOOP
        INSERT INTO notificacion_entrega (notificacion_id, canal, estado)
        VALUES (v_notif_id, v_canal, CASE WHEN v_canal = 'IN_APP' THEN 'ENVIADO' ELSE 'PENDIENTE' END);
    END LOOP;

    RETURN v_notif_id;
END;
$$;


CREATE OR REPLACE FUNCTION app.notificar_reserva_confirmada()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_usuario_id UUID;
    v_nombre     TEXT;
BEGIN
    IF NEW.estado_codigo = 'CONFIRMADA' AND OLD.estado_codigo <> 'CONFIRMADA' AND NEW.cliente_id IS NOT NULL THEN
        SELECT usuario_id, nombre_completo INTO v_usuario_id, v_nombre
        FROM cliente_perfil WHERE id = NEW.cliente_id;

        IF v_usuario_id IS NOT NULL THEN
            PERFORM app.notificar_evento(
                'RESERVA_CONFIRMADA',
                v_usuario_id, NULL,
                'RESERVA', NEW.id,
                'Reserva confirmada',
                'Tu reserva para ' || NEW.nombre_nino || ' el ' || to_char(NEW.fecha_evento, 'DD/MM/YYYY') || ' fue confirmada.',
                '/cliente/mis-reservas/' || NEW.id
            );
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_reserva_notificar_confirmacion
    AFTER UPDATE OF estado_codigo
    ON reserva
    FOR EACH ROW
    EXECUTE FUNCTION app.notificar_reserva_confirmada();


CREATE OR REPLACE FUNCTION app.notificar_evento_solicitado()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_admin RECORD;
BEGIN
    IF NEW.estado_codigo = 'SOLICITADA' AND TG_OP = 'INSERT' THEN
        FOR v_admin IN
            SELECT DISTINCT ur.usuario_id
            FROM usuario_rol ur
            WHERE ur.rol_codigo IN ('SUPERADMIN', 'ADMIN')
        LOOP
            PERFORM app.notificar_evento(
                'EVENTO_SOLICITUD',
                v_admin.usuario_id, NULL,
                'EVENTO', NEW.id,
                'Nueva solicitud de evento',
                'Solicitud recibida para el ' || to_char(NEW.fecha_evento, 'DD/MM/YYYY') || ' en turno ' || NEW.turno_codigo,
                '/admin/eventos/' || NEW.id
            );
        END LOOP;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_evento_notificar_solicitud
    AFTER INSERT ON evento
    FOR EACH ROW
    EXECUTE FUNCTION app.notificar_evento_solicitado();


CREATE OR REPLACE FUNCTION app.tarifa_cerrar_vigencia_anterior()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE tarifa
    SET vigencia_hasta = NEW.vigencia_desde - 1,
        es_activo = FALSE,
        updated_at = NOW()
    WHERE sede_id = NEW.sede_id
      AND tipo_dia_codigo = NEW.tipo_dia_codigo
      AND es_activo = TRUE
      AND deleted_at IS NULL
      AND id <> NEW.id
      AND (vigencia_hasta IS NULL OR vigencia_hasta >= NEW.vigencia_desde);

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_tarifa_cerrar_anterior
    AFTER INSERT ON tarifa
    FOR EACH ROW
    WHEN (NEW.es_activo = TRUE)
    EXECUTE FUNCTION app.tarifa_cerrar_vigencia_anterior();


CREATE OR REPLACE FUNCTION app.actualizar_estado_contrato_pendiente()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_admin RECORD;
    v_evento evento%ROWTYPE;
    v_dias_restantes INT;
BEGIN
    FOR v_evento IN
        SELECT e.* FROM evento e
        LEFT JOIN contrato c ON c.evento_id = e.id
        WHERE e.estado_codigo = 'CONFIRMADA'
          AND e.deleted_at IS NULL
          AND e.fecha_evento BETWEEN app.hoy() AND app.hoy() + INTERVAL '7 days'
          AND (c.id IS NULL OR c.estado_codigo NOT IN ('FIRMADO', 'ARCHIVADO'))
    LOOP
        v_dias_restantes := v_evento.fecha_evento - app.hoy();

        FOR v_admin IN
            SELECT DISTINCT ur.usuario_id
            FROM usuario_rol ur
            WHERE ur.rol_codigo IN ('SUPERADMIN', 'ADMIN')
        LOOP
            IF NOT EXISTS (
                SELECT 1 FROM notificacion
                WHERE tipo_codigo = 'EVENTO_CONTRATO_PENDIENTE'
                  AND entidad_id = v_evento.id
                  AND destinatario_usuario_id = v_admin.usuario_id
                  AND created_at > app.hoy()::timestamp
            ) THEN
                PERFORM app.notificar_evento(
                    'EVENTO_CONTRATO_PENDIENTE',
                    v_admin.usuario_id, NULL,
                    'EVENTO', v_evento.id,
                    'Contrato pendiente',
                    'Evento del ' || to_char(v_evento.fecha_evento, 'DD/MM/YYYY') || ' en ' || v_dias_restantes || ' dias sin contrato firmado',
                    '/admin/eventos/' || v_evento.id
                );
            END IF;
        END LOOP;
    END LOOP;
END;
$$;


CREATE OR REPLACE FUNCTION app.detectar_aforo_limite()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cfg          configuracion_sede%ROWTYPE;
    v_aforo_actual INT;
    v_admin        RECORD;
BEGIN
    IF NEW.estado_codigo NOT IN ('CONFIRMADA', 'PENDIENTE') THEN
        RETURN NEW;
    END IF;

    SELECT * INTO v_cfg FROM configuracion_sede WHERE sede_id = NEW.sede_id;
    v_aforo_actual := app.aforo_ocupado(NEW.sede_id, NEW.fecha_evento);

    IF v_aforo_actual >= (v_cfg.aforo_maximo * 0.9)::INT
       AND v_aforo_actual < v_cfg.aforo_maximo
       AND NEW.fecha_evento BETWEEN app.hoy() AND app.hoy() + INTERVAL '3 days' THEN
        FOR v_admin IN
            SELECT DISTINCT ur.usuario_id
            FROM usuario_rol ur
            WHERE ur.rol_codigo IN ('SUPERADMIN', 'ADMIN')
        LOOP
            IF NOT EXISTS (
                SELECT 1 FROM notificacion
                WHERE tipo_codigo = 'AFORO_LIMITE'
                  AND entidad_id = NEW.id
                  AND destinatario_usuario_id = v_admin.usuario_id
                  AND created_at > (NOW() - INTERVAL '24 hours')
            ) THEN
                PERFORM app.notificar_evento(
                    'AFORO_LIMITE',
                    v_admin.usuario_id, NULL,
                    'RESERVA', NEW.id,
                    'Aforo cercano al limite',
                    'Aforo al ' || ((v_aforo_actual::FLOAT / v_cfg.aforo_maximo) * 100)::INT || '% para ' || to_char(NEW.fecha_evento, 'DD/MM/YYYY'),
                    '/admin/calendario?fecha=' || to_char(NEW.fecha_evento, 'YYYY-MM-DD')
                );
            END IF;
        END LOOP;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_reserva_detectar_aforo
    AFTER INSERT OR UPDATE OF estado_codigo
    ON reserva
    FOR EACH ROW
    EXECUTE FUNCTION app.detectar_aforo_limite();


CREATE OR REPLACE FUNCTION app.limpiar_cache_dni_vencido()
RETURNS INT
LANGUAGE plpgsql
AS $$
DECLARE
    v_eliminados INT;
BEGIN
    DELETE FROM cache_dni WHERE expira_at < NOW();
    GET DIAGNOSTICS v_eliminados = ROW_COUNT;
    RETURN v_eliminados;
END;
$$;


CREATE OR REPLACE FUNCTION app.limpiar_notificaciones_expiradas()
RETURNS INT
LANGUAGE plpgsql
AS $$
DECLARE
    v_eliminados INT;
BEGIN
    DELETE FROM notificacion WHERE expira_at IS NOT NULL AND expira_at < NOW();
    GET DIAGNOSTICS v_eliminados = ROW_COUNT;
    RETURN v_eliminados;
END;
$$;


CREATE OR REPLACE FUNCTION app.limpiar_cliente_tokens_vencidos()
RETURNS INT
LANGUAGE plpgsql
AS $$
DECLARE
    v_eliminados INT;
BEGIN
    DELETE FROM cliente_token
    WHERE expira_at < NOW()
       OR (usado_at IS NOT NULL AND usado_at < NOW() - INTERVAL '30 days');
    GET DIAGNOSTICS v_eliminados = ROW_COUNT;
    RETURN v_eliminados;
END;
$$;


CREATE OR REPLACE FUNCTION app.mantenimiento_diario()
RETURNS TABLE (tarea TEXT, registros_afectados INT)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_anio INT;
    v_mes  INT;
BEGIN
    v_anio := EXTRACT(YEAR FROM NOW() + INTERVAL '2 months')::INT;
    v_mes  := EXTRACT(MONTH FROM NOW() + INTERVAL '2 months')::INT;
    PERFORM app.crear_particion_auditoria(v_anio, v_mes);
    tarea := 'particion_auditoria_creada';
    registros_afectados := 1;
    RETURN NEXT;

    tarea := 'cache_dni_eliminados';
    registros_afectados := app.limpiar_cache_dni_vencido();
    RETURN NEXT;

    tarea := 'notificaciones_expiradas_eliminadas';
    registros_afectados := app.limpiar_notificaciones_expiradas();
    RETURN NEXT;

    tarea := 'cliente_tokens_eliminados';
    registros_afectados := app.limpiar_cliente_tokens_vencidos();
    RETURN NEXT;
END;
$$;