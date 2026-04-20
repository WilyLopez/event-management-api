package com.playzone.pems.application.venta.port.out;

import com.playzone.pems.application.venta.dto.query.VentaQuery;

public interface ImprimirTicketVentaPort {

    byte[] generarTicketPdf(VentaQuery venta);
}