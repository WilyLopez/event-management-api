package com.playzone.pems.infrastructure.persistence.preferencia.jpa;

import com.playzone.pems.infrastructure.persistence.preferencia.entity.PreferenciaAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciaAdminJpaRepository extends JpaRepository<PreferenciaAdminEntity, Long> {

    Optional<PreferenciaAdminEntity> findByUsuarioAdminId(Long idUsuarioAdmin);

    boolean existsByUsuarioAdminId(Long idUsuarioAdmin);

    void deleteByUsuarioAdminId(Long idUsuarioAdmin);
}
