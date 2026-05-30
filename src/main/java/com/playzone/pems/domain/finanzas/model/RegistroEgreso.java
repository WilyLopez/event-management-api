package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEgreso {
    private Long            id;
    private Long            idTipoEgreso;
    private String          nombreTipoEgreso;
    private CategoriaEgreso categoriaEgreso;
    private Long            idSede;
    private BigDecimal    monto;
    private LocalDate     fecha;
    private Integer       periodoAnio;
    private Integer       periodoMes;
    private String        descripcion;
    private String        comprobanteUrl;
    private boolean       esRecurrente;
    private Long          idUsuarioRegistra;
    private LocalDateTime fechaCreacion;
}
