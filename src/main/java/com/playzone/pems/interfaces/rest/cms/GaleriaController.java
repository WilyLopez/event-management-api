package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.port.in.GestionarGaleriaUseCase;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/galeria")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class GaleriaController {

    private final GestionarGaleriaUseCase galeriaUseCase;

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<Void>> subir(
            @PathVariable Long idSede,
            @RequestParam MultipartFile archivo,
            @RequestParam(defaultValue = "GENERAL") CategoriaImagen categoria,
            @RequestParam(required = false) String altTexto,
            @RequestParam(defaultValue = "0") int orden,
            @RequestAttribute Long idUsuarioAdmin) throws IOException {

        galeriaUseCase.subir(
                idSede,
                archivo.getBytes(),
                archivo.getOriginalFilename(),
                archivo.getContentType(),
                altTexto,
                categoria,
                orden,
                idUsuarioAdmin);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.noContent());
    }

    @DeleteMapping("/{idImagen}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idImagen) {
        galeriaUseCase.eliminar(idImagen);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idImagen}/orden")
    public ResponseEntity<ApiResponse<Void>> reordenar(
            @PathVariable Long idImagen,
            @RequestParam int nuevoOrden) {
        galeriaUseCase.reordenar(idImagen, nuevoOrden);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}