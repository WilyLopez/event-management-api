package com.playzone.pems.infrastructure.persistence.venta.jpa;

import com.playzone.pems.infrastructure.persistence.venta.entity.VentaPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface VentaPagoJpaRepository extends JpaRepository<VentaPagoEntity, Long> {
    List<VentaPagoEntity> findByVentaId(Long ventaId);

    @Modifying
    @Query("DELETE FROM VentaPagoEntity vp WHERE vp.ventaId = :ventaId")
    void deleteByVentaId(@Param("ventaId") Long ventaId);

    @Query(value = """
            SELECT COALESCE(SUM(vp.monto), 0)
            FROM venta_pago vp
            JOIN venta v ON vp.venta_id = v.id
            WHERE v.sede_id = :idSede
              AND v.tipo = 'RESERVA'
              AND EXTRACT(YEAR FROM v.fecha_visita) = :anio
              AND EXTRACT(MONTH FROM v.fecha_visita) = :mes
              AND vp.medio_pago_codigo = :medioPago
              AND vp.es_validado = true
            """, nativeQuery = true)
    BigDecimal sumBySedeAndPeriodoAndMedioPago(
            @Param("idSede") Long idSede,
            @Param("anio") int anio,
            @Param("mes") int mes,
            @Param("medioPago") String medioPago);
}
