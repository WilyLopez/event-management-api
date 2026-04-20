package com.playzone.pems.interfaces.scheduler;

import com.playzone.pems.domain.calendario.model.DisponibilidadDiaria;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.domain.usuario.model.Sede;
import com.playzone.pems.domain.usuario.repository.SedeRepository;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisponibilidadScheduler {

    private final SedeRepository               sedeRepository;
    private final DisponibilidadDiariaRepository disponibilidadRepository;

    @Scheduled(cron = "0 5 0 * * *", zone = "America/Lima")
    @Transactional
    public void precrearDisponibilidadFutura() {
        List<Sede> sedes = sedeRepository.findAllActivas();
        LocalDate hoy    = FechaUtil.hoyPeru();

        for (Sede sede : sedes) {
            for (int dias = 1; dias <= 60; dias++) {
                LocalDate fecha = hoy.plusDays(dias);

                boolean yaExiste = disponibilidadRepository
                        .findBySedeAndFecha(sede.getId(), fecha)
                        .isPresent();

                if (!yaExiste) {
                    DisponibilidadDiaria nueva = DisponibilidadDiaria.builder()
                            .idSede(sede.getId())
                            .fecha(fecha)
                            .accesoPublicoActivo(true)
                            .turnoT1Disponible(true)
                            .turnoT2Disponible(true)
                            .aforoPublicoActual(0)
                            .build();
                    disponibilidadRepository.save(nueva);
                }
            }
        }

        log.info("[DisponibilidadScheduler] Precreación completada para {} sede(s) — ventana de 60 días.",
                sedes.size());
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "America/Lima")
    @Transactional
    public void limpiarDiasPasados() {
        LocalDate ayer = FechaUtil.hoyPeru().minusDays(1);
        List<Sede> sedes = sedeRepository.findAllActivas();

        for (Sede sede : sedes) {
            disponibilidadRepository.findBySedeAndFecha(sede.getId(), ayer).ifPresent(d -> {
                if (d.isAccesoPublicoActivo()) {
                    disponibilidadRepository.bloquearAccesoPublico(sede.getId(), ayer);
                }
            });
        }

        log.info("[DisponibilidadScheduler] Limpieza de días pasados completada.");
    }
}