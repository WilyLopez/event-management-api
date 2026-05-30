package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playzone.pems.domain.finanzas.model.enums.EstadoPresupuesto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresupuestoEventoResponse {
    private Long             id;
    private Long             idEventoPrivado;
    private String           concepto;
    private String           categoria;
    private BigDecimal       montoEstimado;
    private BigDecimal       montoReal;
    private EstadoPresupuesto estado;
    private LocalDateTime    fechaCreacion;
    private LocalDateTime    fechaActualizacion;
}
