package com.playzone.pems.infrastructure.persistence.auth.jpa;

import com.playzone.pems.infrastructure.persistence.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revocado = true WHERE r.idUsuario = :idUsuario AND r.tipoUsuario = :tipoUsuario AND r.revocado = false")
    void revocarPorUsuario(@Param("idUsuario") Long idUsuario, @Param("tipoUsuario") String tipoUsuario);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.revocado = true OR r.fechaExpira < :ahora")
    void eliminarExpirados(@Param("ahora") Instant ahora);
}
