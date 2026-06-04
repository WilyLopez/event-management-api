package com.playzone.pems.domain.evento.repository;

import com.playzone.pems.domain.evento.model.PagoVenta;

import java.util.List;

public interface PagoVentaRepository {

    PagoVenta save(PagoVenta pago);

    List<PagoVenta> findByIdVenta(Long idVenta);
}
