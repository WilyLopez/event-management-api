package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.TipoEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TipoEventoJpaRepository extends JpaRepository<TipoEventoEntity, String> {

    List<TipoEventoEntity> findAllByOrderByOrdenAscNombreAsc();

    List<TipoEventoEntity> findByActivoTrueOrderByOrdenAscNombreAsc();

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndCodigoNot(String nombre, String codigo);

    @Query("SELECT COUNT(p) > 0 FROM PaqueteEventoEntity p WHERE p.tipoEventoCodigo = :codigo AND p.deletedAt IS NULL")
    boolean tienePaquetesActivos(@Param("codigo") String codigo);
}
