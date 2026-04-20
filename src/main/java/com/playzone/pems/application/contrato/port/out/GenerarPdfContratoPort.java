package com.playzone.pems.application.contrato.port.out;

public interface GenerarPdfContratoPort {

    String generarYAlmacenar(Long idContrato, String contenidoHtml);
}