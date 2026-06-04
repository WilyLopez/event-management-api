package com.playzone.pems.domain.evento.repository;

import com.playzone.pems.domain.evento.model.VentaPresencial;

import java.util.Optional;

public interface VentaPresencialRepository {

    Optional<VentaPresencial> findById(Long id);

    VentaPresencial save(VentaPresencial venta);
}
