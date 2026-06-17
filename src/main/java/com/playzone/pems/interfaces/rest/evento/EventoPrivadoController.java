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
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final SupabaseAuthFacade              supabaseAuthFacade;

    @GetMapping
    @PreAuthorize("hasAuthority('evento.ver')")
    public ResponseEntity<ApiResponse<Page<EventoPrivadoResponse>>> listar(
            @RequestParam(required = false) Long      idCliente,
            @RequestParam(required = false) Long      idSede,
            @RequestParam(required = false) String    estado,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Pageable pageable) {

        Long idClienteEfectivo = resolverIdCliente(idCliente);
        Page<EventoPrivadoQuery> result;
        if (idClienteEfectivo != null) {
            result = consultarUseCase.consultarPorCliente(idClienteEfectivo, pageable);
        } else if (idSede != null && inicio != null && fin != null) {
            result = consultarUseCase.consultarPorSedeYRangoFechas(idSede, inicio, fin, pageable);
        } else if (idSede != null && estado != null) {
            result = consultarUseCase.consultarPorSedeYEstado(idSede, estado, pageable);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Se requiere idCliente o filtros de sede");
        }
        return ResponseEntity.ok(ApiResponse.ok(result.map(this::toResponse)));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('evento.ver')")
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
    @PreAuthorize("hasAuthority('evento.ver')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(consultarUseCase.consultarPorId(id))));
    }

    @PostMapping("/clientes/{idCliente}/sedes/{idSede}")
    @PreAuthorize("hasAuthority('evento.crear')")
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
    @PreAuthorize("hasAuthority('evento.confirmar')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> confirmar(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmarEventoRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(confirmarUseCase.ejecutar(
                        id,
                        request.getPrecioTotal(),
                        request.getMontoAdelanto(),
                        supabaseAuthFacade.usuarioActualId()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado")),
                        request.getMedioPago()))));
    }

    @PostMapping("/{id}/completar")
    @PreAuthorize("hasAuthority('evento.confirmar')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> completar(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(eventoService.completar(id,
                        supabaseAuthFacade.usuarioActualId()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"))))));
    }

    @PostMapping("/{id}/registrar-saldo")
    @PreAuthorize("hasAuthority('evento.confirmar')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> registrarSaldo(
            @PathVariable Long id,
            @RequestParam BigDecimal monto,
            @RequestParam String medioPago) {
        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(eventoService.registrarSaldo(id, monto, medioPago,
                        supabaseAuthFacade.usuarioActualId()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"))))));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('evento.confirmar')")
    public ResponseEntity<ApiResponse<EventoPrivadoResponse>> cancelar(
            @PathVariable Long id,
            @RequestParam String motivoCancelacion) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(cancelarUseCase.ejecutar(id, motivoCancelacion))));
    }

    @GetMapping("/{idEvento}/checklist")
    @PreAuthorize("hasAuthority('evento.ver')")
    public ResponseEntity<ApiResponse<List<ChecklistEventoQuery>>> listarChecklist(
            @PathVariable Long idEvento) {
        return ResponseEntity.ok(ApiResponse.ok(checklistUseCase.consultarPorEvento(idEvento)));
    }

    @PostMapping("/{idEvento}/checklist/{idChecklist}/completar")
    @PreAuthorize("hasAuthority('evento.confirmar')")
    public ResponseEntity<ApiResponse<ChecklistEventoQuery>> completarTarea(
            @PathVariable Long idEvento,
            @PathVariable Long idChecklist) {

        return ResponseEntity.ok(ApiResponse.ok(
                checklistUseCase.completar(idChecklist,
                        supabaseAuthFacade.usuarioActualId()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado")))));
    }

    @PostMapping("/{idEvento}/checklist/{idChecklist}/descompletar")
    @PreAuthorize("hasAuthority('evento.confirmar')")
    public ResponseEntity<ApiResponse<ChecklistEventoQuery>> descompletarTarea(
            @PathVariable Long idEvento,
            @PathVariable Long idChecklist) {

        return ResponseEntity.ok(ApiResponse.ok(checklistUseCase.descompletar(idChecklist)));
    }

    private Long resolverIdCliente(Long solicitado) {
        if (supabaseAuthFacade.tieneRol("CLIENTE")) {
            Long propio = supabaseAuthFacade.clientePerfilId()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Cliente sin perfil asociado"));
            if (solicitado != null && !solicitado.equals(propio)) {
                throw new AccessDeniedException("No puedes consultar datos de otro cliente");
            }
            return propio;
        }
        return solicitado;
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
                .medioPago(q.getMedioPago())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}
