package com.playzone.pems.infrastructure.scheduler;

import com.playzone.pems.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanup {

    private final RefreshTokenRepository repository;

    @Scheduled(cron = "0 0 3 * * *")
    public void limpiar() {
        log.info("Limpiando refresh tokens expirados o revocados.");
        repository.eliminarExpirados();
    }
}
