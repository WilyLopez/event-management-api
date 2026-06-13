package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.SeccionWebQuery;

import java.util.List;

public interface GestionarSeccionWebUseCase {

    record CrearCommand(String codigo, String nombre, String descripcion, int orden) {}

    record ActualizarCommand(String codigoSeccion, String nombre, String descripcion, int orden) {}

    SeccionWebQuery crear(CrearCommand command);

    SeccionWebQuery actualizar(ActualizarCommand command);

    List<SeccionWebQuery> listar();

    List<SeccionWebQuery> listarActivas();

    void activar(String codigoSeccion);

    void desactivar(String codigoSeccion);

    void eliminar(String codigoSeccion);
}
