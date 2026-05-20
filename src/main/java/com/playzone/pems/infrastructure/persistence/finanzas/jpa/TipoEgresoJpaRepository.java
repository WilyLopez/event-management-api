package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.TipoEgresoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TipoEgresoJpaRepository extends JpaRepository<TipoEgresoEntity, Long> {

    List<TipoEgresoEntity> findByActivoTrue();

    List<TipoEgresoEntity> findByCategoriaAndActivoTrue(CategoriaEgreso categoria);

    @Modifying
    @Query("UPDATE TipoEgresoEntity t SET t.activo = false WHERE t.id = :id")
    void desactivar(@Param("id") Long id);
}
