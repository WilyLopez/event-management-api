package com.playzone.pems.interfaces.rest.cms.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EditarContenidoRequest {

    @NotBlank
    private String valorEs;

    @Size(max = 5000)
    private String valorEn;
}