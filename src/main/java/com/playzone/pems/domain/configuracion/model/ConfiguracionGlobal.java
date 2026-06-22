package com.playzone.pems.domain.configuracion.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionGlobal {

    private String         clave;
    private String         valor;
    private String         descripcion;
    private String         tipo;
    private boolean        esSecreto;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
