package com.playzone.pems.application.proveedor.port.in;

import com.playzone.pems.domain.contrato.model.ContratoProveedor;
import com.playzone.pems.domain.contrato.model.enums.ContratadoPor;

import java.math.BigDecimal;

public interface VincularProveedorContratoUseCase {

    ContratoProveedor vincular(
            Long idContrato,
            Long idProveedor,
            String servicioDescripcion,
            BigDecimal montoAcordado,
            ContratadoPor contratadoPor);

    void desvincular(Long idContratoProveedor);
}