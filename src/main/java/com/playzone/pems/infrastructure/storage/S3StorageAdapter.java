package com.playzone.pems.infrastructure.storage;

import com.playzone.pems.domain.storage.StoragePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@ConditionalOnProperty(name = "storage.tipo", havingValue = "S3")
public class S3StorageAdapter implements StoragePort {

    @Override
    public String guardar(MultipartFile archivo, String carpeta) {
        throw new UnsupportedOperationException("S3StorageAdapter no implementado aun");
    }

    @Override
    public void eliminar(String rutaRelativa) {
        throw new UnsupportedOperationException("S3StorageAdapter no implementado aun");
    }

    @Override
    public String obtenerUrlPublica(String rutaRelativa) {
        throw new UnsupportedOperationException("S3StorageAdapter no implementado aun");
    }
}
