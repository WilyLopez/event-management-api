package com.playzone.pems.infrastructure.persistence.inventario.jpa;

import com.playzone.pems.infrastructure.persistence.inventario.entity.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {

    Page<ProductoEntity> findBySede_Id(Long idSede, Pageable pageable);

    Page<ProductoEntity> findBySede_IdAndCategoria_Id(Long idSede, Long idCategoria, Pageable pageable);

    @Query("SELECT p FROM ProductoEntity p WHERE p.sede.id = :idSede AND LOWER(p.nombre) LIKE LOWER(CONCAT('%',:nombre,'%'))")
    Page<ProductoEntity> findBySedeAndNombre(@Param("idSede") Long idSede, @Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT p FROM ProductoEntity p WHERE p.sede.id = :idSede AND p.activo = true AND p.stockActual <= p.stockMinimo")
    List<ProductoEntity> findEnAlertaDeStock(@Param("idSede") Long idSede);
}