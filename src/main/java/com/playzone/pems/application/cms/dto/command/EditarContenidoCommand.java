package com.playzone.pems.application.cms.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditarContenidoCommand {

    @NotNull
    private final Long idContenido;

    @NotBlank
    private final String valorEs;

    @Size(max = 5000)
    private final String valorEn;

    @NotNull
    private final Long idUsuarioEditor;
}