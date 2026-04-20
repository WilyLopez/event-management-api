package com.playzone.pems.infrastructure.external.storage;

import com.playzone.pems.application.cms.port.out.SubirImagenStoragePort;
import com.playzone.pems.application.contrato.port.out.GenerarPdfContratoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class StorageAdapter implements SubirImagenStoragePort, GenerarPdfContratoPort {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final S3StorageClient s3Client;

    @Override
    public String subir(byte[] contenido, String nombreArchivo, String contentType) {
        String timestamp = LocalDateTime.now().format(FMT);
        String clave = "galeria/" + timestamp + "_" + nombreArchivo;
        return s3Client.subir(contenido, clave, contentType);
    }

    @Override
    public void eliminar(String urlImagen) {
        s3Client.eliminar(urlImagen);
    }

    @Override
    public String generarYAlmacenar(Long idContrato, String contenidoHtml) {
        byte[] pdf = generarPdf(contenidoHtml);
        String clave = "contratos/contrato_" + idContrato + "_" + LocalDateTime.now().format(FMT) + ".pdf";
        return s3Client.subir(pdf, clave, "application/pdf");
    }

    private byte[] generarPdf(String contenidoHtml) {
        return contenidoHtml.getBytes(StandardCharsets.UTF_8);
    }
}