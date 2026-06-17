package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.application.comercial.dto.response.TipoEventoResponse;

import java.util.List;
import java.util.Map;

public interface GestionarTipoEventoUseCase {
    List<TipoEventoResponse> listarTodos();
    List<TipoEventoResponse> listarActivos();
    TipoEventoResponse obtener(String codigo);
    TipoEventoResponse crear(Map<String, Object> datos);
    TipoEventoResponse actualizar(String codigo, Map<String, Object> datos);
    void eliminar(String codigo);
}
