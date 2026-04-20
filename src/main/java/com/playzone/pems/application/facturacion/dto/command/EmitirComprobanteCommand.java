package com.playzone.pems.application.facturacion.dto.command;

import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoDocReceptor;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmitirComprobanteCommand {

    @NotNull
    private final Long idPago;

    @NotNull
    private final Long idSede;

    @NotNull
    private final TipoComprobante tipoComprobante;

    @NotNull
    private final TipoDocReceptor tipoDocReceptor;

    @Size(max = 20)
    private final String nroDocReceptor;

    @Size(max = 200)
    private final String razonSocialReceptor;

    @Size(max = 300)
    private final String direccionReceptor;
}