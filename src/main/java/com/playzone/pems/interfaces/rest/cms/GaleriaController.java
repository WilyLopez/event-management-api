package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.port.in.GestionarGaleriaUseCase;
import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/galeria")
@RequiredArgsConstructor
public class GaleriaController {

    private final GestionarGaleriaUseCase galeriaUseCase;
    private final SupabaseAuthFacade      supabaseAuthFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<GaleriaResponse>>> listar(
            @RequestParam(required = false) Long idSede,
            @RequestParam(defaultValue = "false") boolean soloDestacadas,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long sedeId = idSede;

        Pageable pageable = PageRequest.of(page, size, Sort.by("ordenVisualizacion").ascending());
        Page<GaleriaResponse> resultado = galeriaUseCase.listar(sedeId, soloDestacadas, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(resultado)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sitio.galeria')")
    public ResponseEntity<ApiResponse<GaleriaResponse>> subir(
            @RequestParam MultipartFile archivo,
            @RequestParam(defaultValue = "GENERAL") CategoriaImagen categoria,
            @RequestParam(required = false) String altTexto,
            @RequestParam(defaultValue = "0") int orden,
            @RequestParam(required = false) Long idSede) throws IOException {

        ImagenGaleria imagen = galeriaUseCase.subir(
                idSede,
                archivo.getBytes(),
                archivo.getOriginalFilename(),
                archivo.getContentType(),
                altTexto,
                categoria,
                orden,
                supabaseAuthFacade.usuarioActualId().orElseThrow());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(imagen)));
    }

    @PostMapping("/sedes/{idSede}")
    @PreAuthorize("hasAuthority('sitio.galeria')")
    public ResponseEntity<ApiResponse<GaleriaResponse>> subirConSede(
            @PathVariable Long idSede,
            @RequestParam MultipartFile archivo,
            @RequestParam(defaultValue = "GENERAL") CategoriaImagen categoria,
            @RequestParam(required = false) String altTexto,
            @RequestParam(defaultValue = "0") int orden) throws IOException {

        ImagenGaleria imagen = galeriaUseCase.subir(
                idSede,
                archivo.getBytes(),
                archivo.getOriginalFilename(),
                archivo.getContentType(),
                altTexto,
                categoria,
                orden,
                supabaseAuthFacade.usuarioActualId().orElseThrow());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(imagen)));
    }

    @PutMapping("/{idImagen}")
    @PreAuthorize("hasAuthority('sitio.galeria')")
    public ResponseEntity<ApiResponse<GaleriaResponse>> actualizar(
            @PathVariable Long idImagen,
            @RequestBody ActualizarGaleriaRequest request) {

        ImagenGaleria imagen = galeriaUseCase.actualizar(
                idImagen, request.getAltTexto(), request.getCategoria(), request.getOrden());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(imagen)));
    }

    @DeleteMapping("/{idImagen}")
    @PreAuthorize("hasAuthority('sitio.galeria')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idImagen) {
        galeriaUseCase.eliminar(idImagen);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idImagen}/orden")
    @PreAuthorize("hasAuthority('sitio.galeria')")
    public ResponseEntity<ApiResponse<Void>> reordenar(
            @PathVariable Long idImagen,
            @RequestParam int nuevoOrden) {
        galeriaUseCase.reordenar(idImagen, nuevoOrden);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idImagen}/destacar")
    @PreAuthorize("hasAuthority('sitio.galeria')")
    public ResponseEntity<ApiResponse<Void>> destacar(@PathVariable Long idImagen) {
        galeriaUseCase.destacar(idImagen);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idImagen}/quitar-destacado")
    @PreAuthorize("hasAuthority('sitio.galeria')")
    public ResponseEntity<ApiResponse<Void>> quitarDestacado(@PathVariable Long idImagen) {
        galeriaUseCase.quitarDestacado(idImagen);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private GaleriaResponse toResponse(ImagenGaleria i) {
        return GaleriaResponse.builder()
                .id(i.getId())
                .idSede(i.getIdSede())
                .url(i.getUrlImagen())
                .altTexto(i.getAltTexto())
                .categoria(i.getCategoriaImagen())
                .orden(i.getOrdenVisualizacion())
                .destacada(i.isDestacada())
                .fechaCreacion(i.getFechaSubida())
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ActualizarGaleriaRequest {
        private String          altTexto;
        private CategoriaImagen categoria;
        private Integer         orden;
    }

    @Getter
    @Builder
    public static class GaleriaResponse {
        private Long            id;
        private Long            idSede;
        private String          url;
        private String          altTexto;
        private CategoriaImagen categoria;
        private int             orden;
        private boolean         destacada;
        private OffsetDateTime   fechaCreacion;
    }
}
