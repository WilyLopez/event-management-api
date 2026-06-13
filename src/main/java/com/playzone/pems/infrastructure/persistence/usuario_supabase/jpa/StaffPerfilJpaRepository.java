package com.playzone.pems.infrastructure.persistence.usuario_supabase.jpa;

import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.StaffPerfilEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StaffPerfilJpaRepository extends JpaRepository<StaffPerfilEntity, Long> {

    Optional<StaffPerfilEntity> findByUsuarioIdAndDeletedAtIsNull(UUID usuarioId);
}
