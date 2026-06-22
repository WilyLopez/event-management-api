package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.infrastructure.persistence.finanzas.entity.TipoIngresoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TipoIngresoJpaRepository extends JpaRepository<TipoIngresoEntity, String> {

    @Modifying
    @Query("UPDATE TipoIngresoEntity t SET t.activo = false WHERE t.codigo = :codigo")
    void desactivar(@Param("codigo") String codigo);
}
