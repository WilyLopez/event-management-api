package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.domain.comercial.model.ServicioCotizacion;
import java.util.List;

public interface GestionarServiciosCotizacionUseCase {
    List<ServicioCotizacion> listarActivos();
    List<ServicioCotizacion> listarTodos();
    ServicioCotizacion crear(ServicioCotizacion servicio);
    ServicioCotizacion actualizar(ServicioCotizacion servicio);
    void eliminar(Long id);
}
