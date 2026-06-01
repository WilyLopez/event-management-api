package com.playzone.pems.domain.auth.repository;

import com.playzone.pems.domain.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken save(RefreshToken refreshToken);

    void revocarPorUsuario(Long idUsuario, String tipoUsuario);

    void eliminarExpirados();
}
