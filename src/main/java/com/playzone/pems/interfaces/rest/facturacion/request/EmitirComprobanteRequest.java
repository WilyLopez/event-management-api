package com.playzone.pems.interfaces.rest.facturacion.request;

import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoDocReceptor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmitirComprobanteRequest {

    @NotNull
    private Long idPago;

    @NotNull
    private TipoComprobante tipoComprobante;

    @NotNull
    private TipoDocReceptor tipoDocReceptor;

    @Size(max = 20)
    private String nroDocReceptor;

    @Size(max = 200)
    private String razonSocialReceptor;

    @Size(max = 300)
    private String direccionReceptor;
}