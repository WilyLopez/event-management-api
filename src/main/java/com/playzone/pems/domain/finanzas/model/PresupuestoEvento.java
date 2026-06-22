package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.EstadoPresupuesto;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoEvento {
    private Long             id;
    private Long             idEventoPrivado;
    private String           concepto;
    private String           categoria;
    private BigDecimal       montoEstimado;
    private BigDecimal       montoReal;
    private EstadoPresupuesto estado;
    private UUID             idUsuarioRegistra;
    private UUID             idUsuarioEditor;
    private OffsetDateTime    fechaCreacion;
    private OffsetDateTime    fechaActualizacion;
}
