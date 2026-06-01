package com.playzone.pems.application.auth.service;

import com.playzone.pems.domain.auth.model.RefreshToken;
import com.playzone.pems.domain.auth.repository.RefreshTokenRepository;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${playzone.seguridad.jwt-refresh-admin-idle-ms:7200000}")
    private long adminIdleMs;

    @Value("${playzone.seguridad.jwt-refresh-cliente-idle-ms:2700000}")
    private long clienteIdleMs;

    public RefreshToken crear(Long idUsuario, String correo, String tipoUsuario) {
        repository.revocarPorUsuario(idUsuario, tipoUsuario);

        long idleMs = "ADMIN".equals(tipoUsuario) ? adminIdleMs : clienteIdleMs;

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .idUsuario(idUsuario)
                .correo(correo)
                .tipoUsuario(tipoUsuario)
                .fechaCreacion(Instant.now())
                .fechaExpira(Instant.now().plusMillis(idleMs))
                .revocado(false)
                .build();

        return repository.save(token);
    }

    public RefreshToken renovar(String tokenStr) {
        RefreshToken token = repository.findByToken(tokenStr)
                .orElseThrow(() -> new ValidationException("Refresh token inválido."));

        if (token.estaExpirado()) {
            throw new ValidationException("Sesión expirada por inactividad.");
        }

        long idleMs = "ADMIN".equals(token.getTipoUsuario()) ? adminIdleMs : clienteIdleMs;

        RefreshToken renovado = token.toBuilder()
                .fechaExpira(Instant.now().plusMillis(idleMs))
                .ultimoUso(Instant.now())
                .build();

        return repository.save(renovado);
    }

    public void revocar(String tokenStr) {
        repository.findByToken(tokenStr).ifPresent(t ->
                repository.save(t.toBuilder().revocado(true).build()));
    }
}
