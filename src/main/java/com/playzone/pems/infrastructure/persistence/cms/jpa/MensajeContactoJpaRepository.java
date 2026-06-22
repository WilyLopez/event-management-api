package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.MensajeContactoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MensajeContactoJpaRepository extends JpaRepository<MensajeContactoEntity, Long> {

    @Query("SELECT m FROM MensajeContactoEntity m WHERE m.estado = :estado AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    Page<MensajeContactoEntity> findByEstado(@Param("estado") String estado, Pageable pageable);

    @Query("SELECT m FROM MensajeContactoEntity m WHERE m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    Page<MensajeContactoEntity> findAllNotDeleted(Pageable pageable);
}
