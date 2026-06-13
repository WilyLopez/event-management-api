package com.playzone.pems.application.finanzas.dto.query;

import com.playzone.pems.domain.finanzas.model.enums.EstadoPresupuesto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class PresupuestoEventoQuery {
    private Long             id;
    private Long             idEventoPrivado;
    private String           concepto;
    private String           categoria;
    private BigDecimal       montoEstimado;
    private BigDecimal       montoReal;
    private EstadoPresupuesto estado;
    private OffsetDateTime    fechaCreacion;
    private OffsetDateTime    fechaActualizacion;
}
