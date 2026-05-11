package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.infrastructure.persistence.evento.entity.ChecklistEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistEventoJpaRepository extends JpaRepository<ChecklistEventoEntity, Long> {

    List<ChecklistEventoEntity> findByEventoPrivado_IdOrderByOrdenAsc(Long idEvento);

    int countByEventoPrivado_Id(Long idEvento);

    int countByEventoPrivado_IdAndCompletadaTrue(Long idEvento);
}