package com.playzone.pems.interfaces.scheduler;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.repository.PromocionRepository;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromocionScheduler {

    private final PromocionRepository promocionRepository;

    @Scheduled(cron = "0 10 0 * * *", zone = "America/Lima")
    @Transactional
    public void vencerPromociones() {
        LocalDate hoy = FechaUtil.hoyPeru();
        int pagina = 0;
        int desactivadas = 0;

        Page<Promocion> pagina_ = promocionRepository.findAll(PageRequest.of(pagina, 50));

        while (pagina_.hasContent()) {
            for (Promocion p : pagina_.getContent()) {
                if (p.isActivo() && p.getFechaFin() != null && p.getFechaFin().isBefore(hoy)) {
                    promocionRepository.desactivar(p.getId());
                    desactivadas++;
                }
            }

            if (!pagina_.hasNext()) break;
            pagina++;
            pagina_ = promocionRepository.findAll(PageRequest.of(++pagina, 50));
        }

        if (desactivadas > 0) {
            log.info("[PromocionScheduler] {} promoción(es) vencida(s) desactivada(s).", desactivadas);
        }
    }

    @Scheduled(cron = "0 15 0 * * *", zone = "America/Lima")
    public void notificarPromocionesActivasHoy() {
        LocalDate hoy = FechaUtil.hoyPeru();

        List<Promocion> activas = new ArrayList<>(promocionRepository.findAutomaticasVigentes(
                null, TipoDia.SEMANA, hoy));

        activas.addAll(promocionRepository.findAutomaticasVigentes(
                null, TipoDia.FIN_SEMANA_FERIADO, hoy));

        long nuevas = activas.stream()
                .filter(p -> p.getFechaInicio().isEqual(hoy))
                .count();

        if (nuevas > 0) {
            log.info("[PromocionScheduler] {} promoción(es) nueva(s) entraron en vigencia hoy ({}).",
                    nuevas, hoy);
        }
    }
}