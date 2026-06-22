package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.infrastructure.persistence.evento.entity.EventoExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventoExtraJpaRepository extends JpaRepository<EventoExtraEntity, Long> {

    List<EventoExtraEntity> findByEvento_Id(Long idEvento);

    @Modifying
    @Query("DELETE FROM EventoExtraEntity e WHERE e.evento.id = :idEvento")
    void deleteByEvento_Id(@Param("idEvento") Long idEvento);
}
