package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.usuario.port.in.GestionarUsuarioAdminUseCase;
import com.playzone.pems.domain.usuario.model.UsuarioAdmin;
import com.playzone.pems.interfaces.rest.usuario.response.UsuarioAdminResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {

    private final GestionarUsuarioAdminUseCase gestionarUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioAdminResponse>>> listar() {
        List<UsuarioAdminResponse> lista = gestionarUseCase.listar().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/{idAdmin}")
    public ResponseEntity<ApiResponse<UsuarioAdminResponse>> obtener(@PathVariable Long idAdmin) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(gestionarUseCase.obtener(idAdmin))));
    }

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<Void>> crear(
            @PathVariable Long idSede,
            @RequestBody CrearAdminRequest request) {

        gestionarUseCase.crear(new GestionarUsuarioAdminUseCase.CrearCommand(
                idSede,
                request.getNombre(),
                request.getCorreo(),
                request.getContrasena(),
                request.getRol(),
                request.getTelefono()));

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.noContent());
    }

    @PutMapping("/{idAdmin}")
    public ResponseEntity<ApiResponse<UsuarioAdminResponse>> actualizarPerfil(
            @PathVariable Long idAdmin,
            @Valid @RequestBody ActualizarPerfilRequest request) {

        UsuarioAdmin updated = gestionarUseCase.actualizarPerfil(idAdmin,
                new GestionarUsuarioAdminUseCase.ActualizarPerfilCommand(
                        request.getNombre(), request.getTelefono()));

        return ResponseEntity.ok(ApiResponse.ok(toResponse(updated)));
    }

    @PutMapping("/{idAdmin}/contrasena")
    public ResponseEntity<ApiResponse<Void>> cambiarContrasena(
            @PathVariable Long idAdmin,
            @Valid @RequestBody CambiarContrasenaRequest request) {

        gestionarUseCase.cambiarContrasena(idAdmin,
                new GestionarUsuarioAdminUseCase.CambiarContrasenaCommand(
                        request.getContrasenaActual(), request.getContrasenaNueva()));

        return ResponseEntity.ok(ApiResponse.noContent());
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

    private UsuarioAdminResponse toResponse(UsuarioAdmin a) {
        return UsuarioAdminResponse.builder()
                .id(a.getId())
                .idSede(a.getIdSede())
                .nombre(a.getNombre())
                .correo(a.getCorreo())
                .rol(a.getRol())
                .telefono(a.getTelefono())
                .fotoPerfilUrl(a.getFotoPerfilUrl())
                .activo(a.isActivo())
                .debeCambiarContrasena(a.isDebeCambiarContrasena())
                .intentosFallidos(a.getIntentosFallidos())
                .bloqueadoHasta(a.getBloqueadoHasta())
                .ultimoAcceso(a.getUltimoAcceso())
                .fechaCreacion(a.getFechaCreacion())
                .build();
    }

    /* ─── Request DTOs ──────────────────────────────────────────────────────── */

    @Getter @NoArgsConstructor
    public static class CrearAdminRequest {
        @NotBlank @Size(max = 120) private String nombre;
        @NotBlank @Email           private String correo;
        @NotBlank @Size(min = 8)   private String contrasena;
        private String rol;
        private String telefono;
    }

    @Getter @NoArgsConstructor
    public static class ActualizarPerfilRequest {
        @NotBlank @Size(max = 120) private String nombre;
        private String telefono;
    }

    @Getter @NoArgsConstructor
    public static class CambiarContrasenaRequest {
        @NotBlank private String contrasenaActual;
        @NotBlank @Size(min = 8) private String contrasenaNueva;
    }
}
