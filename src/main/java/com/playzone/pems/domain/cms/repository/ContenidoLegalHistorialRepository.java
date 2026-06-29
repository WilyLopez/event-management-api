package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.ContenidoLegalHistorial;

import java.util.List;

public interface ContenidoLegalHistorialRepository {

    void guardar(ContenidoLegalHistorial historial);

    List<ContenidoLegalHistorial> findByTipo(String tipo);
}
