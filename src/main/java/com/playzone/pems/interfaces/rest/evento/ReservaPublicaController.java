package com.playzone.pems.interfaces.rest.evento;

import com.playzone.pems.application.evento.dto.command.CrearReservaPublicaCommand;
import com.playzone.pems.application.evento.dto.command.ReprogramarReservaCommand;
import com.playzone.pems.application.evento.dto.query.MetricasReservaQuery;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.in.*;
import com.playzone.pems.interfaces.rest.evento.request.CrearReservaRequest;
import com.playzone.pems.interfaces.rest.evento.request.ReprogramarReservaRequest;
import com.playzone.pems.interfaces.rest.evento.response.MetricasReservaResponse;
import com.playzone.pems.interfaces.rest.evento.response.ReservaPublicaResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final CrearReservaPublicaUseCase  crearUseCase;
    private final ReprogramarReservaUseCase   reprogramarUseCase;
    private final CancelarReservaUseCase      cancelarUseCase;
    private final ConsultarReservasUseCase    consultarUseCase;
    private final BuscarReservasAdminUseCase  buscarAdminUseCase;
    private final ConfirmarIngresoUseCase     ingresoUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReservaPublicaResponse>>> listar(
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
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

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReservaPublicaResponse>>> buscarAdmin(
            @RequestParam(required = false)                    Long      idSede,
            @RequestParam(required = false)                    String    estado,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false)                    Boolean   ingresado,
            @RequestParam(required = false)                    Boolean   esReprogramacion,
            @RequestParam(required = false)                    String    search,
            @RequestParam(defaultValue = "0")                  int       page,
            @RequestParam(defaultValue = "20")                 int       size,
            @RequestParam(defaultValue = "fechaEvento,asc")    String    sort) {

        String[]       parts    = sort.split(",");
        Sort.Direction dir      = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                                  ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable       pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

        return ResponseEntity.ok(ApiResponse.ok(
                buscarAdminUseCase.buscar(idSede, estado, fecha, ingresado, esReprogramacion, search, pageable)
                        .map(this::toResponse)));
    }

    @GetMapping("/admin/metricas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MetricasReservaResponse>> metricas(
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        MetricasReservaQuery q = buscarAdminUseCase.metricas(idSede, fecha);
        return ResponseEntity.ok(ApiResponse.ok(MetricasReservaResponse.builder()
                .fecha(q.getFecha())
                .totalReservas(q.getTotalReservas())
                .pendientes(q.getPendientes())
                .confirmadas(q.getConfirmadas())
                .canceladas(q.getCanceladas())
                .ingresados(q.getIngresados())
                .aforoMaximo(q.getAforoMaximo())
                .aforoOcupado(q.getAforoOcupado())
                .aforoRestante(q.getAforoRestante())
                .ingresosDia(q.getIngresosDia())
                .build()));
    }

    @PostMapping("/clientes/{idCliente}/sedes/{idSede}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> crear(
            @PathVariable Long idCliente,
            @PathVariable Long idSede,
            @Valid @RequestBody CrearReservaRequest request) {

        ReservaPublicaQuery query = crearUseCase.ejecutar(
                CrearReservaPublicaCommand.builder()
                        .idCliente(idCliente).idSede(idSede)
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

        return ResponseEntity.ok(ApiResponse.ok(toResponse(
                reprogramarUseCase.ejecutar(ReprogramarReservaCommand.builder()
                        .idReservaOriginal(idReserva)
                        .nuevaFechaEvento(request.getNuevaFechaEvento())
                        .build()))));
    }

    @PostMapping("/{idReserva}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> cancelar(
            @PathVariable Long idReserva,
            @RequestParam String motivo) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(cancelarUseCase.ejecutar(idReserva, motivo))));
    }

    @PostMapping("/{idReserva}/ingreso")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> confirmarIngreso(
            @PathVariable Long idReserva,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(ingresoUseCase.ejecutar(idReserva, idUsuarioAdmin))));
    }

    private ReservaPublicaResponse toResponse(ReservaPublicaQuery q) {
        return ReservaPublicaResponse.builder()
                .id(q.getId())
                .idCliente(q.getIdCliente())
                .nombreCliente(q.getNombreCliente())
                .correoCliente(q.getCorreoCliente())
                .idSede(q.getIdSede())
                .estado(q.getEstado())
                .canalReserva(q.getCanalReserva())
                .tipoDia(q.getTipoDia())
                .fechaEvento(q.getFechaEvento())
                .numeroTicket(q.getNumeroTicket())
                .precioHistorico(q.getPrecioHistorico())
                .descuentoAplicado(q.getDescuentoAplicado())
                .totalPagado(q.getTotalPagado())
                .nombreNino(q.getNombreNino())
                .edadNino(q.getEdadNino())
                .nombreAcompanante(q.getNombreAcompanante())
                .firmoConsentimiento(q.isFirmoConsentimiento())
                .esReprogramacion(q.isEsReprogramacion())
                .vecesReprogramada(q.getVecesReprogramada())
                .ingresado(q.isIngresado())
                .fechaIngreso(q.getFechaIngreso())
                .codigoQr(q.getCodigoQr())
                .medioPago(q.getMedioPago())
                .referenciaPago(q.getReferenciaPago())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}