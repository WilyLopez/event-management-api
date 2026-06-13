package com.playzone.pems.infrastructure.persistence.usuario_supabase.jpa;

import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.PerfilUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerfilUsuarioJpaRepository extends JpaRepository<PerfilUsuarioEntity, UUID> {

    Optional<PerfilUsuarioEntity> findByCorreoAndDeletedAtIsNull(String correo);

    Optional<PerfilUsuarioEntity> findByIdAndDeletedAtIsNull(UUID id);
}
