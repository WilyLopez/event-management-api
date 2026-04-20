package com.playzone.pems.interfaces.rest.inventario.request;

import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MovimientoInventarioRequest {

    @NotNull
    private TipoMovimiento tipoMovimiento;

    @NotNull @Min(1)
    private Integer cantidad;

    @NotBlank @Size(max = 200)
    private String motivo;
}