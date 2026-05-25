package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.application.comercial.dto.command.ActualizarActividadCommand;
import com.playzone.pems.application.comercial.dto.command.CrearActividadCommand;
import com.playzone.pems.application.comercial.dto.query.ActividadLocalQuery;

import java.util.List;

public interface GestionarActividadesUseCase {
    ActividadLocalQuery crear(CrearActividadCommand command);
    ActividadLocalQuery actualizar(ActualizarActividadCommand command);
    List<ActividadLocalQuery> listarTodas();
    List<ActividadLocalQuery> listarActivas();
    List<ActividadLocalQuery> listarEspeciales();
    void eliminar(Long id);
}
