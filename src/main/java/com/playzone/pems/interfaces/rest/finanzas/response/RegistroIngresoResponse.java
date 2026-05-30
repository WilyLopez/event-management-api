package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistroIngresoResponse {
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
    private LocalDateTime    fechaCreacion;
}
