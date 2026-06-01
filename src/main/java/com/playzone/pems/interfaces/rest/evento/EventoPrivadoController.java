package com.playzone.pems.interfaces.rest.evento;

import com.playzone.pems.application.evento.dto.command.SolicitarEventoPrivadoCommand;
import com.playzone.pems.application.evento.dto.query.ChecklistEventoQuery;
import com.playzone.pems.application.evento.dto.query.EventoExtraQuery;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import com.playzone.pems.application.evento.port.in.*;
import com.playzone.pems.application.evento.service.EventoPrivadoService;
import com.playzone.pems.domain.comercial.model.ExtraPaquete;
import com.playzone.pems.domain.comercial.repository.ExtraPaqueteRepository;
import com.playzone.pems.interfaces.rest.evento.request.ConfirmarEventoRequest;
import com.playzone.pems.interfaces.rest.evento.request.SolicitarEventoPrivadoRequest;
import com.playzone.pems.interfaces.rest.evento.response.EventoPrivadoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/eventos-privados")
@RequiredArgsConstructor
public class EventoPrivadoController {

    private final SolicitarEventoPrivadoUseCase   solicitarUseCase;
    private final ConfirmarEventoPrivadoUseCase   confirmarUseCase;
    private final CancelarEventoPrivadoUseCase    cancelarUseCase;
    private final ConsultarEventosPrivadosUseCase consultarUseCase;
    private final BuscarEventosAdminUseCase       buscarAdminUseCase;
    private final GestionarChecklistUseCase       checklistUseCase;
    private final EventoPrivadoService            eventoService;
    private final ExtraPaqueteRepository          extraPaqueteRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<Page<EventoPrivadoResponse>>> listar(
            @RequestParam(required = false) Long      idCliente,
            @RequestParam(required = false) Long      idSede,
            @RequestParam(required = false) String    estado,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestAttribute("idUsuario") Long idUsuario,
            Pageable pageable) {

        Page<EventoPrivadoQuery> result;
        if (idCliente != null) {
            result = consultarUseCase.consultarPorCliente(idCliente, pageable);
        } else if (idSede != null && inicio != null && fin != null) {
            result = consultarUseCase.consultarPorSedeYRangoFechas(idSede, inicio, fin, pageable);
        } else if (idSede != null && estado != null) {
            result = consultarUseCase.consultarPorSedeYEstado(idSede, estado, pageable);
        } else {
            result = consultarUseCase.consultarPorCliente(idUsuario, pageable);
        }
        return ResponseEntity.ok(ApiResponse.ok(result.map(this::toResponse)));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<EventoPrivadoResponse>>> buscarAdmin(
            @RequestParam(required = false)                    Long      idSede,
            @RequestParam(required = false)                    String    estado,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false)                    String    search,
            @RequestParam(defaultValue = "0")                  int       page,
            @RequestParam(defaultValue = "20")                 int       size,
            @RequestParam(defaultValue = "fechaEvento,asc")    String    sort) {

        String[]       parts    = sort.split(",");
        Sort.Direction dir      = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                                  ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable       pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

        return ResponseEntity.ok(ApiResponse.ok(
                buscarAdminUseCase.buscar(idSede, estado, fecha, search, pageable)
                        .map(this::toResponse)));
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

        EventoPrivadoQuery query = solicitarUseCase.ejecutar(
                SolicitarEventoPrivadoCommand.builder()
                        .idCliente(idCliente).idSede(idSede)
                        .idTurno(request.getIdTurno())
                        .fechaEvento(request.getFechaEvento())
                        .tipoEvento(request.getTipoEvento())
                        .contactoAdicional(request.getContactoAdicional())
                        .aforoDeclarado(request.getAforoDeclarado())
                        .nombreNino(request.getNombreNino())
                        .edadCumple(request.getEdadCumple())
                        .idPaquete(request.getIdPaquete())
                        .idsExtras(request.getIdsExtras())
                        .extrasLibres(request.getExtrasLibres())
                        .observaciones(request.getObservaciones())
                        .descripcionPersonalizada(request.getDescripcionPersonalizada())
                        .presupuestoEstimado(request.getPresupuestoEstimado())
                        .idsServiciosCotizacion(request.getIdsServiciosCotizacion())
                        .esCotizacionPersonalizada(request.isEsCotizacionPersonalizada())
                        .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> confirmar(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmarEventoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(confirmarUseCase.ejecutar(
                        id,
                        request.getPrecioTotal(),
                        request.getMontoAdelanto(),
                        request.getMedioPagoAdelanto(),
                        idUsuarioAdmin))));
    }

    @PostMapping("/{id}/completar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> completar(
            @PathVariable Long id,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(eventoService.completar(id, idUsuarioAdmin))));
    }

    @PostMapping("/{id}/registrar-saldo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> registrarSaldo(
            @PathVariable Long id,
            @RequestParam @DecimalMin("0.01") BigDecimal monto,
            @RequestParam String medioPago,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(eventoService.registrarSaldo(id, monto, medioPago, idUsuarioAdmin))));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> cancelar(
            @PathVariable Long id,
            @RequestParam String motivoCancelacion) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(cancelarUseCase.ejecutar(id, motivoCancelacion))));
    }

    @GetMapping("/{idEvento}/checklist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ChecklistEventoQuery>>> listarChecklist(
            @PathVariable Long idEvento) {

        return ResponseEntity.ok(ApiResponse.ok(checklistUseCase.listar(idEvento)));
    }

    @PostMapping("/{idEvento}/checklist/{idChecklist}/completar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChecklistEventoQuery>> completarTarea(
            @PathVariable Long idEvento,
            @PathVariable Long idChecklist,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                checklistUseCase.completar(idChecklist, idUsuarioAdmin)));
    }

    @PostMapping("/{idEvento}/checklist/{idChecklist}/descompletar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChecklistEventoQuery>> descompletarTarea(
            @PathVariable Long idEvento,
            @PathVariable Long idChecklist) {

        return ResponseEntity.ok(ApiResponse.ok(checklistUseCase.descompletar(idChecklist)));
    }

    @GetMapping("/paquetes/{idPaquete}/extras")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listarExtrasPaquete(
            @PathVariable Long idPaquete) {

        List<Map<String, Object>> extras = extraPaqueteRepository.findActivosByPaquete(idPaquete)
                .stream().map(e -> Map.of(
                        "id",          (Object) e.getId(),
                        "nombre",      e.getNombre(),
                        "descripcion", e.getDescripcion() != null ? e.getDescripcion() : "",
                        "orden",       e.getOrden()
                )).toList();

        return ResponseEntity.ok(ApiResponse.ok(extras));
    }

    @PostMapping("/paquetes/{idPaquete}/extras")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> crearExtra(
            @PathVariable Long idPaquete,
            @RequestBody Map<String, Object> body) {

        ExtraPaquete extra = ExtraPaquete.builder()
                .idPaquete(idPaquete)
                .nombre((String) body.get("nombre"))
                .descripcion((String) body.get("descripcion"))
                .activo(true)
                .orden(body.containsKey("orden") ? (Integer) body.get("orden") : 0)
                .build();

        ExtraPaquete guardado = extraPaqueteRepository.save(extra);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                Map.of("id", guardado.getId(), "nombre", guardado.getNombre(),
                        "descripcion", guardado.getDescripcion() != null ? guardado.getDescripcion() : "",
                        "orden", guardado.getOrden())));
    }

    @DeleteMapping("/extras/{idExtra}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarExtra(@PathVariable Long idExtra) {
        extraPaqueteRepository.desactivar(idExtra);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private EventoPrivadoResponse toResponse(EventoPrivadoQuery q) {
        return EventoPrivadoResponse.builder()
                .id(q.getId())
                .idCliente(q.getIdCliente())
                .nombreCliente(q.getNombreCliente())
                .correoCliente(q.getCorreoCliente())
                .telefonoCliente(q.getTelefonoCliente())
                .idSede(q.getIdSede())
                .estado(q.getEstado())
                .idTurno(q.getIdTurno())
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
                .medioPagoAdelanto(q.getMedioPagoAdelanto())
                .notasInternas(q.getNotasInternas())
                .observaciones(q.getObservaciones())
                .nombreNino(q.getNombreNino())
                .edadCumple(q.getEdadCumple())
                .idPaquete(q.getIdPaquete())
                .descripcionPersonalizada(q.getDescripcionPersonalizada())
                .presupuestoEstimado(q.getPresupuestoEstimado())
                .esCotizacionPersonalizada(q.isEsCotizacionPersonalizada())
                .usuarioGestor(q.getUsuarioGestor())
                .estadoOperativo(q.getEstadoOperativo())
                .checklistCompleto(q.isChecklistCompleto())
                .horaInicioReal(q.getHoraInicioReal())
                .horaFinReal(q.getHoraFinReal())
                .extras(q.getExtras())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}
