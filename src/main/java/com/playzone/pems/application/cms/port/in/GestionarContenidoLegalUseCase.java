package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;

import java.util.List;

public interface GestionarContenidoLegalUseCase {

    record ActualizarCommand(
            Long   idContenidoLegal,
            String titulo,
            String contenido,
            Long   idUsuario
    ) {}

    ContenidoLegalQuery obtenerPorTipo(String tipo);

    List<ContenidoLegalQuery> listar();

    ContenidoLegalQuery actualizar(ActualizarCommand command);
}
