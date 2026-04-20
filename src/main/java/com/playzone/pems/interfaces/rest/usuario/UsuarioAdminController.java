package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.usuario.port.in.GestionarUsuarioAdminUseCase;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {

    private final GestionarUsuarioAdminUseCase gestionarUseCase;

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<Void>> crear(
            @PathVariable Long idSede,
            @org.springframework.web.bind.annotation.RequestBody CrearAdminRequest request) {

        gestionarUseCase.crear(new GestionarUsuarioAdminUseCase.CrearCommand(
                idSede, request.getNombre(), request.getCorreo(), request.getContrasena()));

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.noContent());
    }

    @PostMapping("/{idAdmin}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long idAdmin) {
        gestionarUseCase.desactivar(idAdmin);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/{idAdmin}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long idAdmin) {
        gestionarUseCase.activar(idAdmin);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Getter
    @NoArgsConstructor
    public static class CrearAdminRequest {
        @NotBlank @Size(max = 120) private String nombre;
        @NotBlank @Email           private String correo;
        @NotBlank @Size(min = 8)   private String contrasena;
    }
}