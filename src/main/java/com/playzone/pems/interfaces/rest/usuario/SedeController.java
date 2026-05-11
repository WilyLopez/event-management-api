package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.usuario.port.in.GestionarSedeUseCase;
import com.playzone.pems.domain.usuario.model.Sede;
import com.playzone.pems.interfaces.rest.usuario.response.SedeResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sedes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SedeController {

    private final GestionarSedeUseCase gestionarSedeUseCase;

    @GetMapping("/{idSede}")
    public ResponseEntity<ApiResponse<SedeResponse>> obtener(@PathVariable Long idSede) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(gestionarSedeUseCase.obtener(idSede))));
    }

    @PutMapping("/{idSede}")
    public ResponseEntity<ApiResponse<SedeResponse>> actualizar(
            @PathVariable Long idSede,
            @Valid @RequestBody ActualizarSedeRequest request) {

        Sede sede = gestionarSedeUseCase.actualizar(idSede,
                new GestionarSedeUseCase.ActualizarSedeCommand(
                        request.getNombre(),
                        request.getDireccion(),
                        request.getCiudad(),
                        request.getDepartamento(),
                        request.getTelefono(),
                        request.getCorreo(),
                        request.getRuc()));

        return ResponseEntity.ok(ApiResponse.ok(toResponse(sede)));
    }

    private SedeResponse toResponse(Sede s) {
        return SedeResponse.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .ciudad(s.getCiudad())
                .departamento(s.getDepartamento())
                .telefono(s.getTelefono())
                .correo(s.getCorreo())
                .ruc(s.getRuc())
                .activo(s.isActivo())
                .fechaCreacion(s.getFechaCreacion())
                .build();
    }

    @Getter @NoArgsConstructor
    public static class ActualizarSedeRequest {
        @NotBlank @Size(max = 120) private String nombre;
        @NotBlank @Size(max = 300) private String direccion;
        @NotBlank @Size(max = 80)  private String ciudad;
        @NotBlank @Size(max = 80)  private String departamento;
        @Size(max = 20)            private String telefono;
        @Size(max = 120)           private String correo;
        @Size(max = 11)            private String ruc;
    }
}
