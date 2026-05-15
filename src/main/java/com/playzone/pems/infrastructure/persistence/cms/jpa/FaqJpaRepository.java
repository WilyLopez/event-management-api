package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqJpaRepository extends JpaRepository<FaqEntity, Long> {

    List<FaqEntity> findByVisibleTrueOrderByOrdenVisualizacionAsc();
}
