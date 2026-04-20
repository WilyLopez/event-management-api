package com.playzone.pems.domain.facturacion.model;

import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SerieComprobante {

    private Long            id;
    private Long            idSede;
    private TipoComprobante tipoComprobante;
    private String          serie;
    private int             correlativoActual;
    private boolean         activo;

    public String generarProximoNumeroCompleto() {
        return serie + "-" + String.format("%08d", correlativoActual + 1);
    }

    public String ultimoNumeroEmitido() {
        return serie + "-" + String.format("%08d", correlativoActual);
    }

    public boolean esSerieValida() {
        return activo && tipoComprobante.requiereEnvioSunat();
    }
}