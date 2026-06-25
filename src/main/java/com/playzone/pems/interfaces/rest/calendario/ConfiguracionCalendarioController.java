package com.playzone.pems.interfaces.rest.calendario;

import com.playzone.pems.application.calendario.port.in.ConfiguracionCalendarioUseCase;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/calendario")
@RequiredArgsConstructor
public class ConfiguracionCalendarioController {

    private final ConfiguracionCalendarioUseCase configuracionUseCase;

    @GetMapping("/configuracion/sedes/{idSede}")
    @PreAuthorize("hasAuthority('calendario.configurar')")
    public ResponseEntity<ApiResponse<ConfiguracionResponse>> obtener(@PathVariable Long idSede) {
        ConfiguracionCalendario cfg = configuracionUseCase.obtener(idSede);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(cfg)));
    }

    @GetMapping("/configuracion/sedes/{idSede}/publica")
    public ResponseEntity<ApiResponse<ConfiguracionPublicaResponse>> obtenerPublica(@PathVariable Long idSede) {
        ConfiguracionCalendario cfg = configuracionUseCase.obtener(idSede);
        return ResponseEntity.ok(ApiResponse.ok(toPublicaResponse(cfg)));
    }

    @PutMapping("/configuracion/sedes/{idSede}")
    @PreAuthorize("hasAuthority('calendario.configurar')")
    public ResponseEntity<ApiResponse<ConfiguracionResponse>> actualizar(
            @PathVariable Long idSede,
            @Valid @RequestBody ActualizarConfiguracionRequest request) {

        ConfiguracionCalendario actualizada = configuracionUseCase.actualizar(idSede,
                ConfiguracionCalendario.builder()
                        .idSede(idSede)
                        .diasMinReservaPublica(request.diasMinReservaPublica())
                        .diasMaxReservaPublica(request.diasMaxReservaPublica())
                        .diasMinEventoPrivado(request.diasMinEventoPrivado())
                        .diasMaxEventoPrivado(request.diasMaxEventoPrivado())
                        .aforoMaximo(request.aforoMaximo())
                        .horaApertura(LocalTime.parse(request.horaApertura()))
                        .horaCierre(LocalTime.parse(request.horaCierre()))
                        .turnoT1Inicio(LocalTime.parse(request.turnoT1Inicio()))
                        .turnoT1Fin(LocalTime.parse(request.turnoT1Fin()))
                        .turnoT2Inicio(LocalTime.parse(request.turnoT2Inicio()))
                        .turnoT2Fin(LocalTime.parse(request.turnoT2Fin()))
                        .diasOperacion(request.diasOperacion())
                        .rangoMaxBloqueo(request.rangoMaxBloqueo())
                        .edadMinCumple(request.edadMinCumple())
                        .edadMaxCumple(request.edadMaxCumple())
                        .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(actualizada)));
    }

    private ConfiguracionResponse toResponse(ConfiguracionCalendario c) {
        return new ConfiguracionResponse(
                c.getIdSede(),
                c.getDiasMinReservaPublica(),
                c.getDiasMaxReservaPublica(),
                c.getDiasMinEventoPrivado(),
                c.getDiasMaxEventoPrivado(),
                c.getAforoMaximo(),
                c.getHoraApertura().toString(),
                c.getHoraCierre().toString(),
                c.getTurnoT1Inicio().toString(),
                c.getTurnoT1Fin().toString(),
                c.getTurnoT2Inicio().toString(),
                c.getTurnoT2Fin().toString(),
                c.getDiasOperacion(),
                c.getRangoMaxBloqueo(),
                c.getEdadMinCumple(),
                c.getEdadMaxCumple()
        );
    }

    private ConfiguracionPublicaResponse toPublicaResponse(ConfiguracionCalendario c) {
        return new ConfiguracionPublicaResponse(
                c.getDiasMinReservaPublica(),
                c.getDiasMaxReservaPublica(),
                c.getDiasMinEventoPrivado(),
                c.getDiasMaxEventoPrivado(),
                c.getAforoMaximo(),
                c.getHoraApertura().toString(),
                c.getHoraCierre().toString(),
                c.getDiasOperacion(),
                c.getEdadMinCumple(),
                c.getEdadMaxCumple()
        );
    }

    public record ConfiguracionResponse(
            Long   idSede,
            int    diasMinReservaPublica,
            int    diasMaxReservaPublica,
            int    diasMinEventoPrivado,
            int    diasMaxEventoPrivado,
            int    aforoMaximo,
            String horaApertura,
            String horaCierre,
            String turnoT1Inicio,
            String turnoT1Fin,
            String turnoT2Inicio,
            String turnoT2Fin,
            String diasOperacion,
            int    rangoMaxBloqueo,
            int    edadMinCumple,
            int    edadMaxCumple
    ) {}

    public record ConfiguracionPublicaResponse(
            int    diasMinReservaPublica,
            int    diasMaxReservaPublica,
            int    diasMinEventoPrivado,
            int    diasMaxEventoPrivado,
            int    aforoMaximo,
            String horaApertura,
            String horaCierre,
            String diasOperacion,
            int    edadMinCumple,
            int    edadMaxCumple
    ) {}

    public record ActualizarConfiguracionRequest(
            @Min(0) int diasMinReservaPublica,
            @Min(1) int diasMaxReservaPublica,
            @Min(1) int diasMinEventoPrivado,
            @Min(1) int diasMaxEventoPrivado,
            @Min(1) int aforoMaximo,
            @NotBlank String horaApertura,
            @NotBlank String horaCierre,
            @NotBlank String turnoT1Inicio,
            @NotBlank String turnoT1Fin,
            @NotBlank String turnoT2Inicio,
            @NotBlank String turnoT2Fin,
            @NotBlank String diasOperacion,
            @Min(1) int rangoMaxBloqueo,
            @Min(0) int edadMinCumple,
            @Min(0) int edadMaxCumple
    ) {}
}
