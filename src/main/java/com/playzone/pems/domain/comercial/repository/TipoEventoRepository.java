package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.TipoEvento;

import java.util.List;
import java.util.Optional;

public interface TipoEventoRepository {
    List<TipoEvento> listarTodos();
    List<TipoEvento> listarActivos();
    Optional<TipoEvento> buscarPorCodigo(String codigo);
    boolean existePorCodigo(String codigo);
    boolean existePorNombre(String nombre);
    boolean existePorNombreExcluyendo(String nombre, String codigoExcluido);
    boolean tienePaquetesAsociados(String codigo);
    TipoEvento guardar(TipoEvento tipoEvento);
    void eliminar(String codigo);
}
