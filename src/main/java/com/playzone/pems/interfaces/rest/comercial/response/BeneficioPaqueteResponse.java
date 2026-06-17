package com.playzone.pems.interfaces.rest.comercial.response;

import lombok.Builder;
import lombok.Getter;
import java.time.OffsetDateTime;

@Getter
@Builder
public class BeneficioPaqueteResponse {
    private Long id;
    private Long idPaquete;
    private String descripcion;
    private int orden;
    private OffsetDateTime fechaCreacion;
}
