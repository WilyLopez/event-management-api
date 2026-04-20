package com.playzone.pems.domain.facturacion.repository;

import com.playzone.pems.domain.facturacion.model.SerieComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;

import java.util.Optional;

public interface SerieComprobanteRepository {

    Optional<SerieComprobante> findById(Long id);

    Optional<SerieComprobante> findActivaBySedeAndTipo(Long idSede, TipoComprobante tipo);

    SerieComprobante save(SerieComprobante serie);

    int incrementarCorrelativoYRetornar(Long idSerie);
}