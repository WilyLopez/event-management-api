package com.playzone.pems.interfaces.rest.media;

import com.playzone.pems.domain.storage.StoragePort;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private static final long MAX_BYTES = 10L * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp",
            "image/svg+xml", "image/x-icon", "application/pdf"
    );

    private final StoragePort storagePort;
    private final String bucketPublico;

    public MediaController(StoragePort storagePort,
                           @Value("${supabase.storage.bucket-publico}") String bucketPublico) {
        this.storagePort = storagePort;
        this.bucketPublico = bucketPublico;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('catalogo.editar')")
    public ResponseEntity<ApiResponse<MediaResponse>> subir(
            @RequestParam MultipartFile archivo,
            @RequestParam String carpeta) {

        log.debug("Peticion para subir archivo: {}, tamanio: {}, carpeta: {}",
                archivo.getOriginalFilename(), archivo.getSize(), carpeta);

        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacio");
        }
        String tipoMime = archivo.getContentType();
        if (tipoMime == null || !TIPOS_PERMITIDOS.contains(tipoMime)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipoMime);
        }
        if (archivo.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("El archivo supera el limite de 10 MB");
        }

        try {
            byte[] bytes = archivo.getBytes();
            String key = carpeta + "/" + UUID.randomUUID() + "_" + sanitizarNombre(archivo.getOriginalFilename());
            String url = storagePort.upload(bucketPublico, key, bytes, tipoMime);
            log.debug("Archivo subido con exito. URL: {}", url);

            MediaResponse response = MediaResponse.builder()
                    .url(url)
                    .nombreArchivo(archivo.getOriginalFilename())
                    .tamanobytes(archivo.getSize())
                    .tipoMime(tipoMime)
                    .fechaSubida(Instant.now().toString())
                    .build();

            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            log.error("Error al procesar la subida del archivo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al subir el archivo", e);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('catalogo.editar')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@RequestParam String url) {
        storagePort.deleteByUrl(url);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private String sanitizarNombre(String nombre) {
        if (nombre == null) return "archivo";
        return nombre.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }

    @Getter
    @Builder
    public static class MediaResponse {
        private String url;
        private String nombreArchivo;
        private long   tamanobytes;
        private String tipoMime;
        private String fechaSubida;
    }
}
