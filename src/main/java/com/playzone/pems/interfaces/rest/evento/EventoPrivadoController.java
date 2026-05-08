package com.playzone.pems.interfaces.rest.evento;

import com.playzone.pems.application.evento.dto.command.SolicitarEventoPrivadoCommand;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import com.playzone.pems.application.evento.port.in.*;
import com.playzone.pems.interfaces.rest.evento.request.SolicitarEventoPrivadoRequest;
import com.playzone.pems.interfaces.rest.evento.response.EventoPrivadoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/eventos-privados")
@RequiredArgsConstructor
public class EventoPrivadoController {

    private final SolicitarEventoPrivadoUseCase solicitarUseCase;
    private final ConfirmarEventoPrivadoUseCase  confirmarUseCase;
    private final CancelarEventoPrivadoUseCase   cancelarUseCase;
    private final ConsultarEventosPrivadosUseCase consultarUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<Page<EventoPrivadoResponse>>> listar(
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Pageable pageable) {

        Page<EventoPrivadoQuery> result;

        if (idCliente != null) {
            result = consultarUseCase.consultarPorCliente(idCliente, pageable);
        } else if (idSede != null && inicio != null && fin != null) {
            result = consultarUseCase.consultarPorSedeYRangoFechas(idSede, inicio, fin, pageable);
        } else if (idSede != null && estado != null) {
            result = consultarUseCase.consultarPorSedeYEstado(idSede, estado, pageable);
        } else {
            return ResponseEntity.ok(ApiResponse.ok(Page.empty(pageable)));
        }

        return ResponseEntity.ok(ApiResponse.ok(result.map(this::toResponse)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(consultarUseCase.consultarPorId(id))));
    }

    @PostMapping("/clientes/{idCliente}/sedes/{idSede}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> solicitar(
            @PathVariable Long idCliente,
            @PathVariable Long idSede,
            @Valid @RequestBody SolicitarEventoPrivadoRequest request) {

        EventoPrivadoQuery query = solicitarUseCase.ejecutar(SolicitarEventoPrivadoCommand.builder()
                .idCliente(idCliente)
                .idSede(idSede)
                .idTurno(request.getIdTurno())
                .fechaEvento(request.getFechaEvento())
                .tipoEvento(request.getTipoEvento())
                .contactoAdicional(request.getContactoAdicional())
                .aforoDeclarado(request.getAforoDeclarado())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PostMapping("/{idEvento}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> confirmar(
            @PathVariable Long idEvento,
            @RequestParam BigDecimal precioTotal,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(confirmarUseCase.ejecutar(idEvento, precioTotal, idUsuarioAdmin))));
    }

    @PostMapping("/{idEvento}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> cancelar(
            @PathVariable Long idEvento,
            @RequestParam String motivoCancelacion) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(cancelarUseCase.ejecutar(idEvento, motivoCancelacion))));
    }

    private EventoPrivadoResponse toResponse(EventoPrivadoQuery q) {
        return EventoPrivadoResponse.builder()
                .id(q.getId())
                .estado(q.getEstado())
                .turno(q.getTurno())
                .horaInicio(q.getHoraInicio())
                .horaFin(q.getHoraFin())
                .fechaEvento(q.getFechaEvento())
                .tipoEvento(q.getTipoEvento())
                .contactoAdicional(q.getContactoAdicional())
                .aforoDeclarado(q.getAforoDeclarado())
                .precioTotalContrato(q.getPrecioTotalContrato())
                .montoAdelanto(q.getMontoAdelanto())
                .montoSaldo(q.getMontoSaldo())
                .nombreCliente(q.getNombreCliente())
                .correoCliente(q.getCorreoCliente())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}