package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.MovimientoCaja;

import java.util.List;

public interface MovimientoCajaRepository {
    List<MovimientoCaja> findByApertura(Long idAperturaCaja);
    MovimientoCaja save(MovimientoCaja movimiento);
    void deleteById(Long id);
}
