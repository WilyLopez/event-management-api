package com.playzone.pems.domain.venta.repository;

import com.playzone.pems.domain.venta.model.VentaPago;

import java.math.BigDecimal;
import java.util.List;

public interface VentaPagoRepository {
    VentaPago save(VentaPago pago);
    List<VentaPago> findByVentaId(Long ventaId);
    void deleteByVentaId(Long ventaId);
    BigDecimal sumValidadosBySedeAndPeriodoAndMedioPago(Long idSede, int anio, int mes, String medioPago);
}
