package com.playzone.pems.interfaces.rest.calendario;

import com.playzone.pems.application.calendario.port.in.GestionarFeriadoUseCase;
import com.playzone.pems.domain.calendario.model.enums.TipoFeriado;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/feriados")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FeriadoController {

    private final GestionarFeriadoUseCase gestionarUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crear(
            @RequestBody CrearFeriadoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        gestionarUseCase.crear(new GestionarFeriadoUseCase.CrearCommand(
                request.getTipoFeriado(), request.getFecha(), request.getDescripcion(), idUsuarioAdmin));

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.noContent());
    }

    @DeleteMapping("/{idFeriado}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idFeriado) {
        gestionarUseCase.eliminar(idFeriado);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Getter
    @NoArgsConstructor
    public static class CrearFeriadoRequest {
        @NotNull  private TipoFeriado tipoFeriado;
        @NotNull  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fecha;
        @NotBlank private String descripcion;
    }
}