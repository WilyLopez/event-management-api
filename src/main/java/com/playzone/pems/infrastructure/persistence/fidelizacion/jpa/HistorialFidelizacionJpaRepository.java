package com.playzone.pems.infrastructure.persistence.fidelizacion.jpa;

import com.playzone.pems.infrastructure.persistence.fidelizacion.entity.HistorialFidelizacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HistorialFidelizacionJpaRepository extends JpaRepository<HistorialFidelizacionEntity, Long> {

    Optional<HistorialFidelizacionEntity> findByReservaPublica_Id(Long idReservaPublica);

    Page<HistorialFidelizacionEntity> findByCliente_IdOrderByVisitaNumeroDesc(Long idCliente, Pageable pageable);

    @Query("SELECT COUNT(h) FROM HistorialFidelizacionEntity h WHERE h.cliente.id = :idCliente")
    int countByCliente(@Param("idCliente") Long idCliente);

    @Query("SELECT COUNT(h) > 0 FROM HistorialFidelizacionEntity h WHERE h.cliente.id = :idCliente AND h.visitaNumero = :visitaNumero AND h.esBeneficioAplicado = true")
    boolean existsBeneficioAplicadoByClienteAndVisita(
            @Param("idCliente") Long idCliente,
            @Param("visitaNumero") int visitaNumero);
}