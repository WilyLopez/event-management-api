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
public class RegistroEgreso {
    private Long          id;
    private String        tipoEgresoCodigo;
    private Long          idSede;
    private BigDecimal    monto;
    private LocalDate     fecha;
    private Integer       periodoAnio;
    private Integer       periodoMes;
    private String        descripcion;
    private String        comprobanteUrl;
    private boolean       esRecurrente;
    private UUID          idUsuarioRegistra;
    private OffsetDateTime fechaCreacion;
}
