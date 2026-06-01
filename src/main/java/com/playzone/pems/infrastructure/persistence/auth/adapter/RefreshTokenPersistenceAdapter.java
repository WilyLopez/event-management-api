package com.playzone.pems.infrastructure.persistence.auth.adapter;

import com.playzone.pems.domain.auth.model.RefreshToken;
import com.playzone.pems.domain.auth.repository.RefreshTokenRepository;
import com.playzone.pems.infrastructure.persistence.auth.entity.RefreshTokenEntity;
import com.playzone.pems.infrastructure.persistence.auth.jpa.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpa;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpa.findByToken(token).map(this::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return toDomain(jpa.save(toEntity(refreshToken)));
    }

    @Override
    @Transactional
    public void revocarPorUsuario(Long idUsuario, String tipoUsuario) {
        jpa.revocarPorUsuario(idUsuario, tipoUsuario);
    }

    @Override
    @Transactional
    public void eliminarExpirados() {
        jpa.eliminarExpirados(Instant.now());
    }

    private RefreshToken toDomain(RefreshTokenEntity e) {
        return RefreshToken.builder()
                .id(e.getId())
                .token(e.getToken())
                .idUsuario(e.getIdUsuario())
                .correo(e.getCorreo())
                .tipoUsuario(e.getTipoUsuario())
                .fechaCreacion(e.getFechaCreacion())
                .fechaExpira(e.getFechaExpira())
                .revocado(e.isRevocado())
                .ultimoUso(e.getUltimoUso())
                .build();
    }

    private RefreshTokenEntity toEntity(RefreshToken r) {
        return RefreshTokenEntity.builder()
                .id(r.getId())
                .token(r.getToken())
                .idUsuario(r.getIdUsuario())
                .correo(r.getCorreo())
                .tipoUsuario(r.getTipoUsuario())
                .fechaCreacion(r.getFechaCreacion())
                .fechaExpira(r.getFechaExpira())
                .revocado(r.isRevocado())
                .ultimoUso(r.getUltimoUso())
                .build();
    }
}
