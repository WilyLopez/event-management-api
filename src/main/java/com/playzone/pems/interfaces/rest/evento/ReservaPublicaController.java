package com.playzone.pems.interfaces.rest.evento;

import com.playzone.pems.application.evento.dto.command.CrearReservaPublicaCommand;
import com.playzone.pems.application.evento.dto.command.ReprogramarReservaCommand;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.in.*;
import com.playzone.pems.interfaces.rest.evento.request.CrearReservaRequest;
import com.playzone.pems.interfaces.rest.evento.request.ReprogramarReservaRequest;
import com.playzone.pems.interfaces.rest.evento.response.ReservaPublicaResponse;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaPublicaController {

    private final CrearReservaPublicaUseCase crearUseCase;
    private final ReprogramarReservaUseCase  reprogramarUseCase;
    private final CancelarReservaUseCase     cancelarUseCase;
    private final ConsultarReservasUseCase   consultarUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReservaPublicaResponse>>> listar(
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Pageable pageable) {

        Page<ReservaPublicaQuery> result;

        if (idCliente != null) {
            result = consultarUseCase.consultarPorCliente(idCliente, pageable);
        } else if (idSede != null && fecha != null) {
            result = consultarUseCase.consultarPorSedeYFecha(idSede, fecha, pageable);
        } else if (idSede != null && estado != null) {
            result = consultarUseCase.consultarPorSedeYEstado(idSede, estado, pageable);
        } else {
            return ResponseEntity.ok(ApiResponse.ok(Page.empty(pageable)));
        }

        return ResponseEntity.ok(ApiResponse.ok(result.map(this::toResponse)));
    }

    @PostMapping("/clientes/{idCliente}/sedes/{idSede}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> crear(
            @PathVariable Long idCliente,
            @PathVariable Long idSede,
            @Valid @RequestBody CrearReservaRequest request) {

        ReservaPublicaQuery query = crearUseCase.ejecutar(CrearReservaPublicaCommand.builder()
                .idCliente(idCliente)
                .idSede(idSede)
                .canalReserva(request.getCanalReserva())
                .fechaEvento(request.getFechaEvento())
                .nombreNino(request.getNombreNino())
                .edadNino(request.getEdadNino())
                .nombreAcompanante(request.getNombreAcompanante())
                .dniAcompanante(request.getDniAcompanante())
                .firmoConsentimiento(request.getFirmoConsentimiento())
                .idPromocionManual(request.getIdPromocionManual())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PostMapping("/{idReserva}/reprogramar")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> reprogramar(
            @PathVariable Long idReserva,
            @Valid @RequestBody ReprogramarReservaRequest request) {

        ReservaPublicaQuery query = reprogramarUseCase.ejecutar(ReprogramarReservaCommand.builder()
                .idReservaOriginal(idReserva)
                .nuevaFechaEvento(request.getNuevaFechaEvento())
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @PostMapping("/{idReserva}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> cancelar(
            @PathVariable Long idReserva,
            @RequestParam String motivo) {

        return ResponseEntity.ok(ApiResponse.ok(toResponse(cancelarUseCase.ejecutar(idReserva, motivo))));
    }

    private ReservaPublicaResponse toResponse(ReservaPublicaQuery q) {
        return ReservaPublicaResponse.builder()
                .id(q.getId())
                .numeroTicket(q.getNumeroTicket())
                .estado(q.getEstado())
                .tipoDia(q.getTipoDia())
                .fechaEvento(q.getFechaEvento())
                .nombreNino(q.getNombreNino())
                .edadNino(q.getEdadNino())
                .nombreAcompanante(q.getNombreAcompanante())
                .precioHistorico(q.getPrecioHistorico())
                .descuentoAplicado(q.getDescuentoAplicado())
                .totalPagado(q.getTotalPagado())
                .esReprogramacion(q.isEsReprogramacion())
                .vecesReprogramada(q.getVecesReprogramada())
                .firmoConsentimiento(q.isFirmoConsentimiento())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}