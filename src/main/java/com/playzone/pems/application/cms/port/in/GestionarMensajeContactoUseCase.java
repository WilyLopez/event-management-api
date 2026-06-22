package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.domain.cms.model.MensajeContacto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GestionarMensajeContactoUseCase {

    @Getter
    @Builder
    class RegistrarCommand {
        private final String nombre;
        private final String correo;
        private final String telefono;
        private final String asunto;
        private final String mensaje;
        private final String ipOrigen;
        private final String userAgent;
    }

    @Getter
    @Builder
    class ResponderCommand {
        private final Long   idMensaje;
        private final String respuesta;
        private final UUID   idUsuarioAdmin;
    }

    MensajeContacto registrar(RegistrarCommand command);

    MensajeContacto responder(ResponderCommand command);

    MensajeContacto marcarComoLeido(Long idMensaje);

    MensajeContacto marcarComoSpam(Long idMensaje);

    Page<MensajeContacto> listar(String estado, Pageable pageable);

    MensajeContacto obtener(Long idMensaje);

    void eliminar(Long idMensaje);
}
