package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.SeccionWebQuery;

import java.util.List;

public interface GestionarSeccionWebUseCase {

    record CrearCommand(String codigo, String nombre, String descripcion, int ordenVisualizacion) {}

    record ActualizarCommand(Long idSeccion, String nombre, String descripcion, int ordenVisualizacion) {}

    SeccionWebQuery crear(CrearCommand command);

    SeccionWebQuery actualizar(ActualizarCommand command);

    List<SeccionWebQuery> listar();

    List<SeccionWebQuery> listarVisibles();

    void activar(Long idSeccion);

    void desactivar(Long idSeccion);

    void eliminar(Long idSeccion);
}
