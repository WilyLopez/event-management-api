package com.playzone.pems.interfaces.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionLimpiezaJob {

    private final JdbcTemplate jdbc;

    @Scheduled(cron = "0 30 3 * * *", zone = "America/Lima")
    public void limpiezaNocturna() {
        try {
            Integer eliminadas = jdbc.queryForObject(
                    "SELECT app.limpiar_notificaciones_expiradas()", Integer.class);
            log.info("[NotificacionLimpiezaJob] limpiezaNocturna: {} notificacion(es) eliminadas.", eliminadas);
        } catch (Exception e) {
            log.error("[NotificacionLimpiezaJob] Error en limpiezaNocturna: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 4 * * SUN", zone = "America/Lima")
    public void limpiezaSemanal() {
        try {
            Integer eliminadas = jdbc.queryForObject(
                    "SELECT app.limpiar_notificaciones_baja_prioridad(3)", Integer.class);
            log.info("[NotificacionLimpiezaJob] limpiezaSemanal: {} notificacion(es) de baja prioridad eliminadas.", eliminadas);
        } catch (Exception e) {
            log.error("[NotificacionLimpiezaJob] Error en limpiezaSemanal: {}", e.getMessage(), e);
        }
    }
}
