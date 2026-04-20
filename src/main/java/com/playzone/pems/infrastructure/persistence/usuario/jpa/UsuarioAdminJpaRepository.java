package com.playzone.pems.infrastructure.persistence.usuario.jpa;

import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioAdminJpaRepository extends JpaRepository<UsuarioAdminEntity, Long> {

    Optional<UsuarioAdminEntity> findByCorreo(String correo);

    List<UsuarioAdminEntity> findBySede_Id(Long idSede);

    boolean existsByCorreo(String correo);

    @Modifying
    @Query("UPDATE UsuarioAdminEntity u SET u.intentosFallidos = u.intentosFallidos + 1 WHERE u.id = :id")
    void incrementarIntentosFallidos(@Param("id") Long id);

    @Modifying
    @Query("UPDATE UsuarioAdminEntity u SET u.intentosFallidos = 0, u.bloqueadoHasta = NULL WHERE u.id = :id")
    void reiniciarIntentosFallidos(@Param("id") Long id);
}