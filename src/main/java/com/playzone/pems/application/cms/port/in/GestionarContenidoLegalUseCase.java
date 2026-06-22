package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;

import java.util.List;
import java.util.UUID;

public interface GestionarContenidoLegalUseCase {

    record CrearCommand(
            String tipo,
            String titulo,
            String contenido,
            UUID   idUsuario
    ) {}

    record ActualizarCommand(
            String tipo,
            String titulo,
            String contenido,
            UUID   idUsuario
    ) {}

    ContenidoLegalQuery obtenerPorTipo(String tipo);

    List<ContenidoLegalQuery> listar();

    ContenidoLegalQuery crear(CrearCommand command);

    ContenidoLegalQuery actualizar(ActualizarCommand command);

    ContenidoLegalQuery activar(String tipo);

    ContenidoLegalQuery desactivar(String tipo);

    void eliminar(String tipo);
}
