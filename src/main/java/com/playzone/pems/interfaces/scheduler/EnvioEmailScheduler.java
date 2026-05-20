package com.playzone.pems.interfaces.scheduler;

import com.playzone.pems.domain.marketing.model.CampanaEmail;
import com.playzone.pems.domain.marketing.model.EnvioEmail;
import com.playzone.pems.domain.marketing.repository.CampanaEmailRepository;
import com.playzone.pems.domain.marketing.repository.EnvioEmailRepository;
import com.playzone.pems.infrastructure.external.correo.JavaMailCorreoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvioEmailScheduler {

    private static final int LOTE         = 50;
    private static final int MAX_INTENTOS = 3;

    private final CampanaEmailRepository campanaRepo;
    private final EnvioEmailRepository   envioRepo;
    private final JavaMailCorreoClient   correoClient;

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    @Transactional
    public void procesarEnviosPendientes() {
        List<CampanaEmail> campanas = campanaRepo.findProgramadasParaEnviar();

        for (CampanaEmail campana : campanas) {
            campanaRepo.actualizarEstado(campana.getId(), "ENVIANDO");
            procesarLote(campana.getId());
        }

        List<CampanaEmail> enviando = campanaRepo.findAll(
                org.springframework.data.domain.Pageable.unpaged())
                .getContent()
                .stream()
                .filter(c -> "ENVIANDO".equals(c.getEstado()))
                .toList();

        for (CampanaEmail campana : enviando) {
            procesarLote(campana.getId());

            long pendientes = envioRepo.countByCampanaAndEstado(campana.getId(), "PENDIENTE");
            if (pendientes == 0) {
                campanaRepo.actualizarEstado(campana.getId(), "FINALIZADA");
                log.info("Campaña {} finalizada.", campana.getId());
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Lima")
    @Transactional
    public void reintentarFallidos() {
        List<EnvioEmail> fallidos = envioRepo.findParaReintentar(MAX_INTENTOS);
        for (EnvioEmail envio : fallidos) {
            enviarCorreo(envio);
        }
    }

    private void procesarLote(Long idCampana) {
        List<EnvioEmail> pendientes = envioRepo.findPendientesByCampana(idCampana, LOTE);
        int enviados  = 0;
        int fallidos  = 0;

        for (EnvioEmail envio : pendientes) {
            if (enviarCorreo(envio)) {
                enviados++;
            } else {
                fallidos++;
            }
        }

        if (enviados > 0) campanaRepo.incrementarEnviados(idCampana, enviados);
        if (fallidos > 0) campanaRepo.incrementarFallidos(idCampana, fallidos);
    }

    private boolean enviarCorreo(EnvioEmail envio) {
        try {
            correoClient.enviar(envio.getDestinatario(), envio.getAsunto(), "<p>" + envio.getAsunto() + "</p>");
            envioRepo.save(envio.toBuilder()
                    .estado("ENVIADO")
                    .intentos(envio.getIntentos() + 1)
                    .fechaEnvio(Instant.now())
                    .mensajeError(null)
                    .build());
            return true;
        } catch (Exception e) {
            log.warn("Error al enviar correo a {}: {}", envio.getDestinatario(), e.getMessage());
            String nuevoEstado = (envio.getIntentos() + 1 >= MAX_INTENTOS) ? "REBOTADO" : "ERROR";
            envioRepo.save(envio.toBuilder()
                    .estado(nuevoEstado)
                    .intentos(envio.getIntentos() + 1)
                    .mensajeError(e.getMessage())
                    .build());
            return false;
        }
    }
}
