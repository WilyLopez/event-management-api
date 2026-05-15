package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.domain.cms.model.Resena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ModerarResenaUseCase {

    record SubmitCommand(
            Long   idCliente,
            Long   idEventoPrivado,
            String nombreAutor,
            String contenido,
            int    calificacion,
            String fotoUrl
    ) {}

    record ResponderCommand(Long idResena, String respuesta, Long idUsuarioAdmin) {}

    Page<Resena> listar(boolean pendientes, Pageable pageable);

    Resena obtener(Long idResena);

    Resena submit(SubmitCommand command);

    Resena aprobar(Long idResena, Long idUsuarioAdmin);

    Resena responder(ResponderCommand command);

    void destacar(Long idResena);

    void quitarDestacado(Long idResena);

    void toggleMostrarHome(Long idResena, boolean mostrar);

    void rechazar(Long idResena);
}
