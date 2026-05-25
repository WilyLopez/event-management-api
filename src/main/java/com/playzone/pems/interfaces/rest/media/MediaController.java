package com.playzone.pems.interfaces.rest.media;

import com.playzone.pems.domain.storage.StoragePort;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MediaController {

    private static final long MAX_BYTES = 10L * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp",
            "image/svg+xml", "image/x-icon", "application/pdf"
    );

    private final StoragePort storagePort;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<MediaResponse>> subir(
            @RequestParam MultipartFile archivo,
            @RequestParam String carpeta) {

        log.debug("Petición para subir archivo: {}, tamaño: {}, carpeta: {}",
                archivo.getOriginalFilename(), archivo.getSize(), carpeta);

        if (archivo.isEmpty()) {
            log.warn("Intento de subir archivo vacío");
            throw new IllegalArgumentException("El archivo no puede estar vacio");
        }
        String tipoMime = archivo.getContentType();
        log.debug("Tipo MIME detectado: {}", tipoMime);

        if (tipoMime == null || !TIPOS_PERMITIDOS.contains(tipoMime)) {
            log.warn("Tipo de archivo no permitido: {}", tipoMime);
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipoMime);
        }
        if (archivo.getSize() > MAX_BYTES) {
            log.warn("El archivo supera el limite de 10 MB: {} bytes", archivo.getSize());
            throw new IllegalArgumentException("El archivo supera el limite de 10 MB");
        }

        try {
            String rutaRelativa = storagePort.guardar(archivo, carpeta);
            String url = storagePort.obtenerUrlPublica(rutaRelativa);
            log.debug("Archivo guardado con éxito. URL: {}", url);

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
            throw e;
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> eliminar(@RequestParam String rutaRelativa) {
        storagePort.eliminar(rutaRelativa);
        return ResponseEntity.ok(ApiResponse.noContent());
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
