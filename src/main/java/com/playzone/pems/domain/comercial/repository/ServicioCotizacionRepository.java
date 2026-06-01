package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.ServicioCotizacion;

import java.util.List;

public interface ServicioCotizacionRepository {
    List<ServicioCotizacion> findAllActivos();
}
