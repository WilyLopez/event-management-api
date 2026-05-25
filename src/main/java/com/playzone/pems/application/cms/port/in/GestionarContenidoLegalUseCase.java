package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;

import java.util.List;

public interface GestionarContenidoLegalUseCase {

    record CrearCommand(
            String tipo,
            String titulo,
            String contenido,
            Long   idUsuario
    ) {}

    record ActualizarCommand(
            String tipo,
            String titulo,
            String contenido,
            Long   idUsuario
    ) {}

    ContenidoLegalQuery obtenerPorTipo(String tipo);

    List<ContenidoLegalQuery> listar();

    ContenidoLegalQuery crear(CrearCommand command);

    ContenidoLegalQuery actualizar(ActualizarCommand command);

    ContenidoLegalQuery activar(String tipo);

    ContenidoLegalQuery desactivar(String tipo);

    void eliminar(String tipo);
}
