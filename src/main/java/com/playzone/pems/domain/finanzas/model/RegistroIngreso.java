package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroIngreso {
    private Long             id;
    private Long             idTipoIngreso;
    private String           nombreTipoIngreso;
    private CategoriaIngreso categoriaIngreso;
    private Long             idSede;
    private Long             idReservaPublica;
    private Long             idEventoPrivado;
    private BigDecimal       monto;
    private LocalDate        fecha;
    private String           medioPago;
    private String           descripcion;
    private boolean          esAutomatico;
    private Long             idUsuarioRegistra;
    private LocalDateTime    fechaCreacion;
}
