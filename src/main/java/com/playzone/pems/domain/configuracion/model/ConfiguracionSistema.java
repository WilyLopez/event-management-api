package com.playzone.pems.domain.configuracion.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSistema {

    private Long           id;
    private String         clave;
    private String         valor;
    private String         descripcion;
    private String         tipo;
    private OffsetDateTime fechaActualizacion;
}
