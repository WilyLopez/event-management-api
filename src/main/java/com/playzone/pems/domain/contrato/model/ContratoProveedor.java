package com.playzone.pems.domain.contrato.model;

import com.playzone.pems.domain.contrato.model.enums.ContratadoPor;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContratoProveedor {

    private Long          id;
    private Long          idContrato;
    private Long          idProveedor;
    private String        servicioDescripcion;
    private BigDecimal    montoAcordado;
    private ContratadoPor contratadoPor;

    public boolean generaCostoParaPlayzone() {
        return ContratadoPor.EMPRESA == contratadoPor
                && montoAcordado != null
                && montoAcordado.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean tieneMonto() {
        return montoAcordado != null;
    }
}