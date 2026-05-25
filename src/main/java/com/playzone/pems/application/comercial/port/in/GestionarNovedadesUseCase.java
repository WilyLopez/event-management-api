package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.application.comercial.dto.command.ActualizarNovedadCommand;
import com.playzone.pems.application.comercial.dto.command.CrearNovedadCommand;
import com.playzone.pems.application.comercial.dto.query.NovedadLocalQuery;

import java.util.List;

public interface GestionarNovedadesUseCase {
    NovedadLocalQuery crear(CrearNovedadCommand command);
    NovedadLocalQuery actualizar(ActualizarNovedadCommand command);
    List<NovedadLocalQuery> listarTodas();
    List<NovedadLocalQuery> listarActivas();
    List<NovedadLocalQuery> listarVisiblesHome();
    void eliminar(Long id);
}
