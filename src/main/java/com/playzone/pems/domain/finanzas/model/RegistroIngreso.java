package com.playzone.pems.domain.finanzas.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroIngreso {
    private Long             id;
    private String           tipoIngresoCodigo;
    private Long             idSede;
    private Long             idReservaPublica;
    private Long             idEventoPrivado;
    private BigDecimal       monto;
    private LocalDate        fecha;
    private String           medioPago;
    private String           descripcion;
    private boolean          esAutomatico;
    private UUID             idUsuarioRegistra;
    private OffsetDateTime    fechaCreacion;
}
