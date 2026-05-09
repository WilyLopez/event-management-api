package com.playzone.pems.interfaces.rest.calendario;

import com.playzone.pems.application.calendario.dto.command.BloquearFechasCommand;
import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.dto.query.ResumenDiaQuery;
import com.playzone.pems.application.calendario.port.in.BloquearFechasUseCase;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.application.calendario.port.in.ConsultarResumenDiaUseCase;
import com.playzone.pems.interfaces.rest.calendario.request.BloquearFechasRequest;
import com.playzone.pems.interfaces.rest.calendario.response.DisponibilidadResponse;
import com.playzone.pems.interfaces.rest.calendario.response.ResumenDiaResponse;
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
    private final ConsultarResumenDiaUseCase     resumenDiaUseCase;
    private final BloquearFechasUseCase          bloquearUseCase;

    @GetMapping("/sedes/{idSede}/disponibilidad")
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> consultarPorFecha(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return ResponseEntity.ok(
                ApiResponse.ok(toDisponibilidadResponse(
                        consultarUseCase.consultarPorFecha(idSede, fecha))));
    }

    @GetMapping("/sedes/{idSede}/disponibilidad/rango")
    public ResponseEntity<ApiResponse<List<DisponibilidadResponse>>> consultarRango(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        List<DisponibilidadResponse> lista = consultarUseCase
                .consultarRango(idSede, inicio, fin)
                .stream()
                .map(this::toDisponibilidadResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/sedes/{idSede}/resumen-dia")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ResumenDiaResponse>> resumenDia(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return ResponseEntity.ok(
                ApiResponse.ok(toResumenDiaResponse(
                        resumenDiaUseCase.ejecutar(idSede, fecha))));
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
                .tipoBloqueo(request.getTipoBloqueo())
                .motivo(request.getMotivo())
                .build());

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/bloqueos/{idBloque}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarBloqueo(
            @PathVariable Long idBloque) {

        bloquearUseCase.desactivar(idBloque);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private DisponibilidadResponse toDisponibilidadResponse(DisponibilidadQuery q) {
        return DisponibilidadResponse.builder()
                .idSede(q.getIdSede())
                .fecha(q.getFecha())
                .tipoDia(q.getTipoDia())
                .esFeriado(q.isEsFeriado())
                .descripcionFeriado(q.getDescripcionFeriado())
                .accesoPublicoActivo(q.isAccesoPublicoActivo())
                .turnoT1Disponible(q.isTurnoT1Disponible())
                .turnoT2Disponible(q.isTurnoT2Disponible())
                .aforoPublicoActual(q.getAforoPublicoActual())
                .aforoMaximo(q.getAforoMaximo())
                .plazasDisponibles(q.getPlazasDisponibles())
                .aforoCompleto(q.isAforoCompleto())
                .bloqueadoManualmente(q.isBloqueadoManualmente())
                .tipoBloqueo(q.getTipoBloqueo())
                .motivoBloqueo(q.getMotivoBloqueo())
                .totalReservas(q.getTotalReservas())
                .totalEventos(q.getTotalEventos())
                .ingresoEstimado(q.getIngresoEstimado())
                .tieneNotas(q.isTieneNotas())
                .ocupacionPorcentaje(q.getOcupacionPorcentaje())
                .build();
    }

    private ResumenDiaResponse toResumenDiaResponse(ResumenDiaQuery q) {
        return ResumenDiaResponse.builder()
                .fecha(q.getFecha())
                .totalReservas(q.getTotalReservas())
                .totalEventos(q.getTotalEventos())
                .ingresoEstimado(q.getIngresoEstimado())
                .pagosPendientes(q.getPagosPendientes())
                .aforoPublicoActual(q.getAforoPublicoActual())
                .aforoMaximo(q.getAforoMaximo())
                .turnoT1(toTurnoResponse(q.getTurnoT1()))
                .turnoT2(toTurnoResponse(q.getTurnoT2()))
                .reservas(q.getReservas().stream().map(r ->
                        ResumenDiaResponse.ReservaResumen.builder()
                                .id(r.getId())
                                .numeroTicket(r.getNumeroTicket())
                                .nombreNino(r.getNombreNino())
                                .nombreCliente(r.getNombreCliente())
                                .estado(r.getEstado())
                                .totalPagado(r.getTotalPagado())
                                .build()).toList())
                .eventos(q.getEventos().stream().map(this::toEventoResumen).toList())
                .alertas(q.getAlertas().stream().map(a ->
                        ResumenDiaResponse.AlertaResumen.builder()
                                .tipo(a.getTipo())
                                .mensaje(a.getMensaje())
                                .nivel(a.getNivel())
                                .build()).toList())
                .build();
    }

    private ResumenDiaResponse.TurnoResponse toTurnoResponse(
            ResumenDiaQuery.ResumenTurno t) {
        return ResumenDiaResponse.TurnoResponse.builder()
                .disponible(t.isDisponible())
                .totalReservas(t.getTotalReservas())
                .eventoPrivado(t.getEventoPrivado() != null
                        ? toEventoResumen(t.getEventoPrivado()) : null)
                .build();
    }

    private ResumenDiaResponse.EventoResumen toEventoResumen(
            ResumenDiaQuery.ResumenEventoQuery e) {
        return ResumenDiaResponse.EventoResumen.builder()
                .id(e.getId())
                .tipoEvento(e.getTipoEvento())
                .turno(e.getTurno())
                .horaInicio(e.getHoraInicio())
                .horaFin(e.getHoraFin())
                .nombreCliente(e.getNombreCliente())
                .estado(e.getEstado())
                .aforoDeclarado(e.getAforoDeclarado())
                .build();
    }
}