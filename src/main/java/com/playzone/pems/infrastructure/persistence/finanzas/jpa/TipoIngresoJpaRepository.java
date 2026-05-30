package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.TipoIngresoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TipoIngresoJpaRepository extends JpaRepository<TipoIngresoEntity, Long> {

    Optional<TipoIngresoEntity> findFirstByCategoriaAndActivoTrue(CategoriaIngreso categoria);

    @Modifying
    @Query("UPDATE TipoIngresoEntity t SET t.activo = false WHERE t.id = :id")
    void desactivar(@Param("id") Long id);
}
