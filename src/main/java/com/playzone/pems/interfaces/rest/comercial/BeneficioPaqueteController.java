package com.playzone.pems.interfaces.rest.comercial;

import com.playzone.pems.application.comercial.port.in.GestionarBeneficiosUseCase;
import com.playzone.pems.domain.comercial.model.BeneficioPaquete;
import com.playzone.pems.interfaces.rest.comercial.response.BeneficioPaqueteResponse;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paquetes/{idPaquete}/beneficios")
@RequiredArgsConstructor
public class BeneficioPaqueteController {

    private final GestionarBeneficiosUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BeneficioPaqueteResponse>>> listar(@PathVariable Long idPaquete) {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarPorPaquete(idPaquete).stream().map(this::toResponse).toList()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<BeneficioPaqueteResponse>> crear(
            @PathVariable Long idPaquete,
            @RequestBody BeneficioPaquete b) {
        BeneficioPaquete nuevo = BeneficioPaquete.builder()
                .idPaquete(idPaquete)
                .descripcion(b.getDescripcion())
                .orden(b.getOrden())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(useCase.crear(nuevo))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<BeneficioPaqueteResponse>> actualizar(
            @PathVariable Long idPaquete,
            @PathVariable Long id,
            @RequestBody BeneficioPaquete b) {
        BeneficioPaquete actualizado = BeneficioPaquete.builder()
                .id(id)
                .idPaquete(idPaquete)
                .descripcion(b.getDescripcion())
                .orden(b.getOrden())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.actualizar(actualizado))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long idPaquete,
            @PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private BeneficioPaqueteResponse toResponse(BeneficioPaquete b) {
        return BeneficioPaqueteResponse.builder()
                .id(b.getId())
                .idPaquete(b.getIdPaquete())
                .descripcion(b.getDescripcion())
                .orden(b.getOrden())
                .fechaCreacion(b.getCreatedAt())
                .build();
    }
}
