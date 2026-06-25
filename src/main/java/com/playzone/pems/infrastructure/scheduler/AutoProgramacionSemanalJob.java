package com.playzone.pems.infrastructure.scheduler;

import com.playzone.pems.application.calendario.port.in.ProgramacionSemanalUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Activa automáticamente la programacion semanal al inicio de cada semana
 * para las sedes que no la configuraron manualmente.
 *
 * Se ejecuta los lunes a las 00:01 (zona Lima). Si no hay cobertura para la
 * semana en curso, la crea marcada como auto_generada=true.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoProgramacionSemanalJob {

    private final ProgramacionSemanalUseCase programacionUseCase;

    @Scheduled(cron = "0 1 0 * * MON", zone = "America/Lima")
    public void activarSemanaActual() {
        log.info("[AutoProgramacion] Iniciando verificacion de cobertura semanal...");
        try {
            programacionUseCase.autoActivarSemanaActual();
            log.info("[AutoProgramacion] Verificacion completada.");
        } catch (Exception ex) {
            log.error("[AutoProgramacion] Error inesperado: {}", ex.getMessage(), ex);
        }
    }
}
