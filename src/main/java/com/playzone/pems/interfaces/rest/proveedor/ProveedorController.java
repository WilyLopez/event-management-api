package com.playzone.pems.interfaces.rest.proveedor;

import com.playzone.pems.application.proveedor.dto.command.GestionarProveedorCommand;
import com.playzone.pems.application.proveedor.dto.query.ProveedorQuery;
import com.playzone.pems.application.proveedor.port.in.GestionarProveedorUseCase;
import com.playzone.pems.interfaces.rest.proveedor.request.GestionarProveedorRequest;
import com.playzone.pems.interfaces.rest.proveedor.response.ProveedorResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProveedorController {

    private final GestionarProveedorUseCase gestionarUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ProveedorResponse>> crear(
            @Valid @RequestBody GestionarProveedorRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(gestionarUseCase.crear(buildCommand(request)))));
    }

    @PutMapping("/{idProveedor}")
    public ResponseEntity<ApiResponse<ProveedorResponse>> actualizar(
            @PathVariable Long idProveedor,
            @Valid @RequestBody GestionarProveedorRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(gestionarUseCase.actualizar(idProveedor, buildCommand(request)))));
    }

    @DeleteMapping("/{idProveedor}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long idProveedor) {
        gestionarUseCase.desactivar(idProveedor);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private GestionarProveedorCommand buildCommand(GestionarProveedorRequest r) {
        return GestionarProveedorCommand.builder()
                .nombre(r.getNombre())
                .ruc(r.getRuc())
                .contactoNombre(r.getContactoNombre())
                .contactoTelefono(r.getContactoTelefono())
                .contactoCorreo(r.getContactoCorreo())
                .tipoServicio(r.getTipoServicio())
                .notas(r.getNotas())
                .build();
    }

    private ProveedorResponse toResponse(ProveedorQuery q) {
        return ProveedorResponse.builder()
                .id(q.getId())
                .nombre(q.getNombre())
                .ruc(q.getRuc())
                .contactoNombre(q.getContactoNombre())
                .contactoTelefono(q.getContactoTelefono())
                .contactoCorreo(q.getContactoCorreo())
                .tipoServicio(q.getTipoServicio())
                .activo(q.isActivo())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}