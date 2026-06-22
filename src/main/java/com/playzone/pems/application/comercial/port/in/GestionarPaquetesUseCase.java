package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.application.comercial.dto.command.ActualizarPaqueteCommand;
import com.playzone.pems.application.comercial.dto.command.CrearPaqueteCommand;
import com.playzone.pems.application.comercial.dto.query.PaqueteEventoQuery;

import java.util.List;

public interface GestionarPaquetesUseCase {
    PaqueteEventoQuery crear(CrearPaqueteCommand command);
    PaqueteEventoQuery actualizar(ActualizarPaqueteCommand command);
    PaqueteEventoQuery obtenerPorId(Long id);
    List<PaqueteEventoQuery> listarTodos();
    List<PaqueteEventoQuery> listarActivos();
    void eliminar(Long id);
    PaqueteEventoQuery subirImagen(Long id, String url);
    PaqueteEventoQuery reordenar(Long id, int nuevoOrden);
}
