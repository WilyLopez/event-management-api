package com.playzone.pems.application.venta.port.out;

import com.playzone.pems.application.venta.dto.query.VentaDetalleQuery;

public interface EnviarDocumentosVentaPort {

    void enviarDocumentos(String destinatario, VentaDetalleQuery ventaDetalle);
}
