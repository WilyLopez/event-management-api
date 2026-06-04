ALTER TABLE perfil_usuario        ENABLE ROW LEVEL SECURITY;
ALTER TABLE preferencia_usuario   ENABLE ROW LEVEL SECURITY;
ALTER TABLE usuario_rol           ENABLE ROW LEVEL SECURITY;
ALTER TABLE cliente_perfil        ENABLE ROW LEVEL SECURITY;
ALTER TABLE staff_perfil          ENABLE ROW LEVEL SECURITY;
ALTER TABLE cache_dni             ENABLE ROW LEVEL SECURITY;

ALTER TABLE sede                  ENABLE ROW LEVEL SECURITY;
ALTER TABLE configuracion_sede    ENABLE ROW LEVEL SECURITY;
ALTER TABLE configuracion_global  ENABLE ROW LEVEL SECURITY;
ALTER TABLE configuracion_publica ENABLE ROW LEVEL SECURITY;

ALTER TABLE feriado               ENABLE ROW LEVEL SECURITY;
ALTER TABLE bloque_calendario     ENABLE ROW LEVEL SECURITY;
ALTER TABLE tarifa                ENABLE ROW LEVEL SECURITY;

ALTER TABLE paquete               ENABLE ROW LEVEL SECURITY;
ALTER TABLE paquete_beneficio     ENABLE ROW LEVEL SECURITY;
ALTER TABLE paquete_extra         ENABLE ROW LEVEL SECURITY;
ALTER TABLE servicio_cotizacion   ENABLE ROW LEVEL SECURITY;
ALTER TABLE promocion             ENABLE ROW LEVEL SECURITY;
ALTER TABLE promocion_marketing   ENABLE ROW LEVEL SECURITY;

ALTER TABLE zona_juego            ENABLE ROW LEVEL SECURITY;
ALTER TABLE zona_juego_medio      ENABLE ROW LEVEL SECURITY;
ALTER TABLE actividad             ENABLE ROW LEVEL SECURITY;
ALTER TABLE novedad               ENABLE ROW LEVEL SECURITY;
ALTER TABLE banner                ENABLE ROW LEVEL SECURITY;
ALTER TABLE galeria_imagen        ENABLE ROW LEVEL SECURITY;
ALTER TABLE faq                   ENABLE ROW LEVEL SECURITY;
ALTER TABLE contenido_legal       ENABLE ROW LEVEL SECURITY;
ALTER TABLE contenido_web         ENABLE ROW LEVEL SECURITY;
ALTER TABLE mensaje_contacto      ENABLE ROW LEVEL SECURITY;
ALTER TABLE resena                ENABLE ROW LEVEL SECURITY;

ALTER TABLE venta                 ENABLE ROW LEVEL SECURITY;
ALTER TABLE venta_pago            ENABLE ROW LEVEL SECURITY;
ALTER TABLE evento                ENABLE ROW LEVEL SECURITY;
ALTER TABLE evento_extra          ENABLE ROW LEVEL SECURITY;
ALTER TABLE evento_servicio       ENABLE ROW LEVEL SECURITY;
ALTER TABLE checklist_evento      ENABLE ROW LEVEL SECURITY;
ALTER TABLE contrato              ENABLE ROW LEVEL SECURITY;
ALTER TABLE contrato_documento    ENABLE ROW LEVEL SECURITY;
ALTER TABLE contrato_actividad    ENABLE ROW LEVEL SECURITY;
ALTER TABLE reserva               ENABLE ROW LEVEL SECURITY;
ALTER TABLE reserva_promocion     ENABLE ROW LEVEL SECURITY;
ALTER TABLE fidelizacion          ENABLE ROW LEVEL SECURITY;

ALTER TABLE apertura_caja         ENABLE ROW LEVEL SECURITY;
ALTER TABLE movimiento_caja       ENABLE ROW LEVEL SECURITY;
ALTER TABLE registro_ingreso      ENABLE ROW LEVEL SECURITY;
ALTER TABLE registro_egreso       ENABLE ROW LEVEL SECURITY;
ALTER TABLE presupuesto_evento    ENABLE ROW LEVEL SECURITY;
ALTER TABLE gasto_evento          ENABLE ROW LEVEL SECURITY;
ALTER TABLE gasto_operativo_diario ENABLE ROW LEVEL SECURITY;

ALTER TABLE serie_comprobante     ENABLE ROW LEVEL SECURITY;
ALTER TABLE comprobante           ENABLE ROW LEVEL SECURITY;

ALTER TABLE notificacion          ENABLE ROW LEVEL SECURITY;
ALTER TABLE notificacion_entrega  ENABLE ROW LEVEL SECURITY;
ALTER TABLE preferencia_notificacion ENABLE ROW LEVEL SECURITY;
ALTER TABLE plantilla_email       ENABLE ROW LEVEL SECURITY;
ALTER TABLE campana_email         ENABLE ROW LEVEL SECURITY;
ALTER TABLE envio_email           ENABLE ROW LEVEL SECURITY;

ALTER TABLE auditoria_log         ENABLE ROW LEVEL SECURITY;
ALTER TABLE cliente_token         ENABLE ROW LEVEL SECURITY;


CREATE POLICY perfil_propio_read  ON perfil_usuario FOR SELECT TO authenticated USING (id = auth.uid() OR app.es_staff());
CREATE POLICY perfil_propio_write ON perfil_usuario FOR UPDATE TO authenticated USING (id = auth.uid() OR app.es_admin());
CREATE POLICY perfil_admin_all    ON perfil_usuario FOR ALL    TO authenticated USING (app.es_superadmin()) WITH CHECK (app.es_superadmin());

CREATE POLICY pref_propio ON preferencia_usuario FOR ALL TO authenticated USING (usuario_id = auth.uid()) WITH CHECK (usuario_id = auth.uid());

CREATE POLICY usuario_rol_propio_read ON usuario_rol FOR SELECT TO authenticated USING (usuario_id = auth.uid() OR app.es_admin());
CREATE POLICY usuario_rol_admin_write ON usuario_rol FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('rol.gestionar')) WITH CHECK (app.usuario_tiene_permiso('rol.gestionar'));

CREATE POLICY cliente_perfil_propio ON cliente_perfil FOR SELECT TO authenticated USING (usuario_id = auth.uid() OR app.es_staff());
CREATE POLICY cliente_perfil_staff  ON cliente_perfil FOR ALL    TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY cliente_perfil_self_update ON cliente_perfil FOR UPDATE TO authenticated USING (usuario_id = auth.uid()) WITH CHECK (usuario_id = auth.uid());

CREATE POLICY staff_perfil_propio ON staff_perfil FOR SELECT TO authenticated USING (usuario_id = auth.uid() OR app.es_admin());
CREATE POLICY staff_perfil_admin  ON staff_perfil FOR ALL    TO authenticated USING (app.es_admin()) WITH CHECK (app.es_admin());

CREATE POLICY cache_dni_staff ON cache_dni FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());


CREATE POLICY rol_read       ON rol      FOR SELECT TO authenticated USING (TRUE);
CREATE POLICY rol_admin      ON rol      FOR ALL    TO authenticated USING (app.es_superadmin()) WITH CHECK (app.es_superadmin());
CREATE POLICY permiso_read   ON permiso  FOR SELECT TO authenticated USING (TRUE);
CREATE POLICY permiso_admin  ON permiso  FOR ALL    TO authenticated USING (app.es_superadmin()) WITH CHECK (app.es_superadmin());
CREATE POLICY rolperm_read   ON rol_permiso FOR SELECT TO authenticated USING (TRUE);
CREATE POLICY rolperm_admin  ON rol_permiso FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('rol.gestionar')) WITH CHECK (app.usuario_tiene_permiso('rol.gestionar'));


DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN SELECT unnest(ARRAY[
        'estado_reserva','estado_evento','estado_contrato','estado_comprobante','estado_caja',
        'medio_pago','tipo_dia','canal_reserva','turno','tipo_feriado','tipo_promocion',
        'tipo_comprobante','tipo_documento','tipo_ingreso','tipo_egreso','tipo_evento',
        'tipo_notificacion','seccion_web','tipo_contenido','tipo_email','segmento_cliente'
    ])
    LOOP
        EXECUTE format('ALTER TABLE %I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('CREATE POLICY %I ON %I FOR SELECT TO anon, authenticated USING (TRUE)', t || '_read', t);
        EXECUTE format('CREATE POLICY %I ON %I FOR ALL TO authenticated USING (app.usuario_tiene_permiso(''catalogo.editar'')) WITH CHECK (app.usuario_tiene_permiso(''catalogo.editar''))', t || '_admin', t);
    END LOOP;
END$$;


CREATE POLICY sede_read  ON sede FOR SELECT TO anon, authenticated USING (deleted_at IS NULL);
CREATE POLICY sede_admin ON sede FOR ALL    TO authenticated USING (app.es_admin()) WITH CHECK (app.es_admin());

CREATE POLICY cfg_sede_read  ON configuracion_sede FOR SELECT TO authenticated USING (app.es_staff());
CREATE POLICY cfg_sede_admin ON configuracion_sede FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('configuracion.editar')) WITH CHECK (app.usuario_tiene_permiso('configuracion.editar'));

CREATE POLICY cfg_global_read  ON configuracion_global FOR SELECT TO authenticated USING (app.es_staff() AND es_secreto = FALSE);
CREATE POLICY cfg_global_admin ON configuracion_global FOR ALL    TO authenticated USING (app.es_superadmin()) WITH CHECK (app.es_superadmin());

CREATE POLICY cfg_publica_read  ON configuracion_publica FOR SELECT TO anon, authenticated USING (TRUE);
CREATE POLICY cfg_publica_write ON configuracion_publica FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.publica')) WITH CHECK (app.usuario_tiene_permiso('sitio.publica'));


CREATE POLICY feriado_read  ON feriado FOR SELECT TO anon, authenticated USING (deleted_at IS NULL);
CREATE POLICY feriado_admin ON feriado FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('calendario.feriado')) WITH CHECK (app.usuario_tiene_permiso('calendario.feriado'));

CREATE POLICY bloque_read  ON bloque_calendario FOR SELECT TO authenticated USING (app.es_staff() AND deleted_at IS NULL);
CREATE POLICY bloque_admin ON bloque_calendario FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('calendario.bloquear')) WITH CHECK (app.usuario_tiene_permiso('calendario.bloquear'));

CREATE POLICY tarifa_read  ON tarifa FOR SELECT TO anon, authenticated USING (deleted_at IS NULL AND es_activo = TRUE);
CREATE POLICY tarifa_admin ON tarifa FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('tarifa.gestionar')) WITH CHECK (app.usuario_tiene_permiso('tarifa.gestionar'));


CREATE POLICY paquete_read  ON paquete FOR SELECT TO anon, authenticated USING (deleted_at IS NULL AND es_activo = TRUE OR app.es_staff());
CREATE POLICY paquete_admin ON paquete FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('paquete.gestionar')) WITH CHECK (app.usuario_tiene_permiso('paquete.gestionar'));

CREATE POLICY beneficio_read  ON paquete_beneficio FOR SELECT TO anon, authenticated USING (TRUE);
CREATE POLICY beneficio_admin ON paquete_beneficio FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('paquete.gestionar')) WITH CHECK (app.usuario_tiene_permiso('paquete.gestionar'));

CREATE POLICY pextra_read  ON paquete_extra FOR SELECT TO anon, authenticated USING (es_activo = TRUE OR app.es_staff());
CREATE POLICY pextra_admin ON paquete_extra FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('paquete.gestionar')) WITH CHECK (app.usuario_tiene_permiso('paquete.gestionar'));

CREATE POLICY servicio_read  ON servicio_cotizacion FOR SELECT TO anon, authenticated USING (es_activo = TRUE OR app.es_staff());
CREATE POLICY servicio_admin ON servicio_cotizacion FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('paquete.gestionar')) WITH CHECK (app.usuario_tiene_permiso('paquete.gestionar'));

CREATE POLICY promo_read  ON promocion FOR SELECT TO anon, authenticated USING (deleted_at IS NULL AND es_activo = TRUE OR app.es_staff());
CREATE POLICY promo_admin ON promocion FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('promocion.gestionar')) WITH CHECK (app.usuario_tiene_permiso('promocion.gestionar'));

CREATE POLICY promo_mkt_read  ON promocion_marketing FOR SELECT TO anon, authenticated USING (TRUE);
CREATE POLICY promo_mkt_admin ON promocion_marketing FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('promocion.gestionar')) WITH CHECK (app.usuario_tiene_permiso('promocion.gestionar'));


CREATE POLICY zona_read  ON zona_juego FOR SELECT TO anon, authenticated USING (es_activa = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY zona_admin ON zona_juego FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.zona')) WITH CHECK (app.usuario_tiene_permiso('sitio.zona'));
CREATE POLICY zona_medio_read  ON zona_juego_medio FOR SELECT TO anon, authenticated USING (TRUE);
CREATE POLICY zona_medio_admin ON zona_juego_medio FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.zona')) WITH CHECK (app.usuario_tiene_permiso('sitio.zona'));

CREATE POLICY actividad_read  ON actividad FOR SELECT TO anon, authenticated USING (es_activa = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY actividad_admin ON actividad FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.actividad')) WITH CHECK (app.usuario_tiene_permiso('sitio.actividad'));

CREATE POLICY novedad_read  ON novedad FOR SELECT TO anon, authenticated USING (es_activa = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY novedad_admin ON novedad FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.novedad')) WITH CHECK (app.usuario_tiene_permiso('sitio.novedad'));

CREATE POLICY banner_read  ON banner FOR SELECT TO anon, authenticated USING (es_activo = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY banner_admin ON banner FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.banner')) WITH CHECK (app.usuario_tiene_permiso('sitio.banner'));

CREATE POLICY galeria_read  ON galeria_imagen FOR SELECT TO anon, authenticated USING (es_activa = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY galeria_admin ON galeria_imagen FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.galeria')) WITH CHECK (app.usuario_tiene_permiso('sitio.galeria'));

CREATE POLICY faq_read  ON faq FOR SELECT TO anon, authenticated USING (es_visible = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY faq_admin ON faq FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.faq')) WITH CHECK (app.usuario_tiene_permiso('sitio.faq'));

CREATE POLICY legal_read  ON contenido_legal FOR SELECT TO anon, authenticated USING (es_activo = TRUE);
CREATE POLICY legal_admin ON contenido_legal FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.legal')) WITH CHECK (app.usuario_tiene_permiso('sitio.legal'));

CREATE POLICY contenido_web_read  ON contenido_web FOR SELECT TO anon, authenticated USING (es_visible = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY contenido_web_admin ON contenido_web FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.contenido')) WITH CHECK (app.usuario_tiene_permiso('sitio.contenido'));

CREATE POLICY mensaje_insert ON mensaje_contacto FOR INSERT TO anon, authenticated WITH CHECK (TRUE);
CREATE POLICY mensaje_read   ON mensaje_contacto FOR SELECT TO authenticated USING (app.usuario_tiene_permiso('sitio.mensaje'));
CREATE POLICY mensaje_admin  ON mensaje_contacto FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.mensaje')) WITH CHECK (app.usuario_tiene_permiso('sitio.mensaje'));

CREATE POLICY resena_insert    ON resena FOR INSERT TO authenticated WITH CHECK (cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()));
CREATE POLICY resena_read_pub  ON resena FOR SELECT TO anon, authenticated USING (es_aprobada = TRUE AND deleted_at IS NULL OR app.es_staff());
CREATE POLICY resena_admin     ON resena FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('sitio.resena')) WITH CHECK (app.usuario_tiene_permiso('sitio.resena'));


CREATE POLICY venta_staff_all ON venta FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY venta_cliente_propia ON venta FOR SELECT TO authenticated USING (cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()));

CREATE POLICY venta_pago_staff ON venta_pago FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY venta_pago_cliente_read ON venta_pago FOR SELECT TO authenticated
    USING (venta_id IN (SELECT id FROM venta WHERE cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())));

CREATE POLICY evento_staff   ON evento FOR ALL    TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY evento_cliente ON evento FOR SELECT TO authenticated USING (cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()));
CREATE POLICY evento_solicitar ON evento FOR INSERT TO authenticated WITH CHECK (cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()));

CREATE POLICY evento_extra_staff ON evento_extra FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY evento_extra_cliente_read ON evento_extra FOR SELECT TO authenticated USING (evento_id IN (SELECT id FROM evento WHERE cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())));

CREATE POLICY evento_servicio_staff ON evento_servicio FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY evento_servicio_cliente_read ON evento_servicio FOR SELECT TO authenticated USING (evento_id IN (SELECT id FROM evento WHERE cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())));

CREATE POLICY checklist_staff ON checklist_evento FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());

CREATE POLICY contrato_staff ON contrato FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY contrato_cliente_read ON contrato FOR SELECT TO authenticated USING (evento_id IN (SELECT id FROM evento WHERE cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())));

CREATE POLICY contrato_doc_staff ON contrato_documento FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY contrato_act_staff ON contrato_actividad FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());

CREATE POLICY reserva_staff   ON reserva FOR ALL    TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY reserva_cliente ON reserva FOR SELECT TO authenticated USING (cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()));

CREATE POLICY reserva_promo_staff ON reserva_promocion FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());

CREATE POLICY fideliz_staff   ON fidelizacion FOR ALL    TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY fideliz_cliente ON fidelizacion FOR SELECT TO authenticated USING (cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()));


CREATE POLICY caja_apertura_staff ON apertura_caja FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY caja_movimiento_staff ON movimiento_caja FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());

CREATE POLICY ingreso_staff_read  ON registro_ingreso FOR SELECT TO authenticated USING (app.es_staff());
CREATE POLICY ingreso_admin_write ON registro_ingreso FOR ALL    TO authenticated USING (app.usuario_tiene_permiso('ingreso.crear')) WITH CHECK (app.usuario_tiene_permiso('ingreso.crear'));

CREATE POLICY egreso_admin ON registro_egreso FOR ALL TO authenticated USING (app.usuario_tiene_permiso('egreso.ver')) WITH CHECK (app.usuario_tiene_permiso('egreso.crear'));

CREATE POLICY presupuesto_staff ON presupuesto_evento FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY gasto_evento_staff ON gasto_evento FOR ALL TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY gasto_op_admin ON gasto_operativo_diario FOR ALL TO authenticated USING (app.es_admin()) WITH CHECK (app.es_admin());


CREATE POLICY serie_admin ON serie_comprobante FOR ALL TO authenticated USING (app.es_admin()) WITH CHECK (app.es_admin());
CREATE POLICY comprobante_staff   ON comprobante FOR ALL    TO authenticated USING (app.es_staff()) WITH CHECK (app.es_staff());
CREATE POLICY comprobante_cliente ON comprobante FOR SELECT TO authenticated USING (
    venta_id IN (SELECT id FROM venta WHERE cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()))
    OR evento_id IN (SELECT id FROM evento WHERE cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()))
);


CREATE POLICY notif_destinatario ON notificacion FOR SELECT TO authenticated USING (
    destinatario_usuario_id = auth.uid()
    OR destinatario_cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())
);
CREATE POLICY notif_destinatario_update ON notificacion FOR UPDATE TO authenticated USING (
    destinatario_usuario_id = auth.uid()
    OR destinatario_cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())
);

CREATE POLICY notif_entrega_staff ON notificacion_entrega FOR SELECT TO authenticated USING (app.es_staff());
CREATE POLICY pref_notif_propia ON preferencia_notificacion FOR ALL TO authenticated USING (
    usuario_id = auth.uid()
    OR cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())
) WITH CHECK (
    usuario_id = auth.uid()
    OR cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid())
);

CREATE POLICY plantilla_email_admin ON plantilla_email FOR ALL TO authenticated USING (app.usuario_tiene_permiso('marketing.plantilla')) WITH CHECK (app.usuario_tiene_permiso('marketing.plantilla'));
CREATE POLICY campana_email_admin   ON campana_email   FOR ALL TO authenticated USING (app.usuario_tiene_permiso('marketing.campana'))  WITH CHECK (app.usuario_tiene_permiso('marketing.campana'));
CREATE POLICY envio_email_admin     ON envio_email     FOR SELECT TO authenticated USING (app.usuario_tiene_permiso('marketing.campana'));


CREATE POLICY auditoria_read_admin ON auditoria_log FOR SELECT TO authenticated USING (app.usuario_tiene_permiso('auditoria.ver'));
CREATE POLICY auditoria_read_propia ON auditoria_log FOR SELECT TO authenticated USING (usuario_id = auth.uid());

CREATE POLICY cliente_token_propio ON cliente_token FOR ALL TO authenticated USING (
    cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()) OR app.es_staff()
) WITH CHECK (
    cliente_id IN (SELECT id FROM cliente_perfil WHERE usuario_id = auth.uid()) OR app.es_staff()
);