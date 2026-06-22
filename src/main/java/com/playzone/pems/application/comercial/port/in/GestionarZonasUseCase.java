package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.application.comercial.dto.command.ActualizarZonaCommand;
import com.playzone.pems.application.comercial.dto.command.CrearZonaCommand;
import com.playzone.pems.application.comercial.dto.query.ZonaJuegoQuery;

import java.util.List;

public interface GestionarZonasUseCase {
    ZonaJuegoQuery crear(CrearZonaCommand command);
    ZonaJuegoQuery actualizar(ActualizarZonaCommand command);
    List<ZonaJuegoQuery> listarTodas();
    List<ZonaJuegoQuery> listarActivas();
    void eliminar(Long id);
    ZonaJuegoQuery agregarMedia(Long id, String url, String tipo);
    ZonaJuegoQuery eliminarMedia(Long id, String url);
    ZonaJuegoQuery reordenar(Long id, int nuevoOrden);
}
