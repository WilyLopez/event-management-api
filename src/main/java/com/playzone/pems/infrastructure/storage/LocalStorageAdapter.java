package com.playzone.pems.infrastructure.storage;

import com.playzone.pems.domain.storage.StoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "storage.tipo", havingValue = "LOCAL", matchIfMissing = true)
public class LocalStorageAdapter implements StoragePort {

    @Value("${storage.local.ruta-base:/uploads}")
    private String rutaBase;

    @Value("${storage.local.url-base:/files}")
    private String urlBase;

    @Override
    public String guardar(MultipartFile archivo, String carpeta) {
        String nombreFinal = UUID.randomUUID() + "_" + sanitizarNombre(archivo.getOriginalFilename());
        Path destino = Paths.get(rutaBase, carpeta, nombreFinal);
        log.debug("Intentando guardar archivo en: {}", destino.toAbsolutePath());
        try {
            Files.createDirectories(destino.getParent());
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Archivo guardado exitosamente en: {}", destino);
        } catch (IOException e) {
            log.error("Error al guardar archivo en storage local: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }
        return "/" + carpeta + "/" + nombreFinal;
    }

    @Override
    public void eliminar(String rutaRelativa) {
        Path ruta = Paths.get(rutaBase + rutaRelativa);
        try {
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            log.warn("No se pudo eliminar archivo: {} — {}", rutaRelativa, e.getMessage());
        }
    }

    @Override
    public String obtenerUrlPublica(String rutaRelativa) {
        return urlBase + rutaRelativa;
    }

    private String sanitizarNombre(String nombre) {
        if (nombre == null) return "archivo";
        return nombre.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }
}
