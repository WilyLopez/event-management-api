package com.playzone.pems.application.inventario.dto.command;

import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovimientoInventarioCommand {

    @NotNull
    private final Long idProducto;

    @NotNull
    private final TipoMovimiento tipoMovimiento;

    @NotNull
    @Min(1)
    private final Integer cantidad;

    @NotBlank
    @Size(max = 200)
    private final String motivo;

    private final Long idVenta;

    @NotNull
    private final Long idUsuario;
}