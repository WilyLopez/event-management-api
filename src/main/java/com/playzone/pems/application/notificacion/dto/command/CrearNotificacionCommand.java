package com.playzone.pems.application.notificacion.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class CrearNotificacionCommand {

    @NotBlank
    private final String tipoCodigo;

    private final UUID destinatarioUsuarioId;

    private final Long destinatarioClienteId;

    @Size(max = 50)
    private final String entidadTipo;

    private final Long entidadId;

    private final Map<String, String> datosExtra;

    @Size(max = 500)
    private final String urlAccion;

    private final String metadata;
}
