package com.playzone.pems.interfaces.rest.calendario;

import com.playzone.pems.application.calendario.dto.command.BloquearFechasCommand;
import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.port.in.BloquearFechasUseCase;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.interfaces.rest.calendario.request.BloquearFechasRequest;
import com.playzone.pems.interfaces.rest.calendario.response.DisponibilidadResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendario")
@RequiredArgsConstructor
public class CalendarioController {

    private final ConsultarDisponibilidadUseCase consultarUseCase;
    private final BloquearFechasUseCase          bloquearUseCase;

    @GetMapping("/sedes/{idSede}/disponibilidad")
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> consultarPorFecha(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return ResponseEntity.ok(ApiResponse.ok(toResponse(consultarUseCase.consultarPorFecha(idSede, fecha))));
    }

    @GetMapping("/sedes/{idSede}/disponibilidad/rango")
    public ResponseEntity<ApiResponse<List<DisponibilidadResponse>>> consultarRango(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        List<DisponibilidadResponse> lista = consultarUseCase.consultarRango(idSede, inicio, fin)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @PostMapping("/sedes/{idSede}/bloqueos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> bloquearFechas(
            @PathVariable Long idSede,
            @Valid @RequestBody BloquearFechasRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        bloquearUseCase.ejecutar(BloquearFechasCommand.builder()
                .idSede(idSede)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .motivo(request.getMotivo())
                .build());

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/bloqueos/{idBloque}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarBloqueo(@PathVariable Long idBloque) {
        bloquearUseCase.desactivar(idBloque);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private DisponibilidadResponse toResponse(DisponibilidadQuery q) {
        return DisponibilidadResponse.builder()
                .idSede(q.getIdSede())
                .fecha(q.getFecha())
                .tipoDia(q.getTipoDia())
                .accesoPublicoActivo(q.isAccesoPublicoActivo())
                .turnoT1Disponible(q.isTurnoT1Disponible())
                .turnoT2Disponible(q.isTurnoT2Disponible())
                .aforoPublicoActual(q.getAforoPublicoActual())
                .aforoMaximo(q.getAforoMaximo())
                .plazasDisponibles(q.getPlazasDisponibles())
                .aforoCompleto(q.isAforoCompleto())
                .bloqueadoManualmente(q.isBloqueadoManualmente())
                .build();
    }
}