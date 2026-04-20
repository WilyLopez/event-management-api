package com.playzone.pems.infrastructure.external.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageClient {

    private final S3Client s3Client;

    @Value("${playzone.storage.bucket}")
    private String bucket;

    @Value("${playzone.storage.url-publica}")
    private String urlPublica;

    public String subir(byte[] contenido, String clave, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(clave)
                .contentType(contentType)
                .contentLength((long) contenido.length)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(contenido));
        String url = urlPublica.endsWith("/") ? urlPublica + clave : urlPublica + "/" + clave;
        log.info("Archivo subido al storage: {}", url);
        return url;
    }

    public void eliminar(String url) {
        try {
            String clave = URI.create(url).getPath().replaceFirst("^/", "");
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(clave).build());
            log.info("Archivo eliminado del storage: {}", clave);
        } catch (Exception e) {
            log.warn("No se pudo eliminar el archivo del storage: {} — {}", url, e.getMessage());
        }
    }
}