package com.playzone.pems.domain.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {
    String guardar(MultipartFile archivo, String carpeta);
    void eliminar(String rutaRelativa);
    String obtenerUrlPublica(String rutaRelativa);
}
