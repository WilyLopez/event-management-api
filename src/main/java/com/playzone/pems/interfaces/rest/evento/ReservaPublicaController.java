package com.playzone.pems.interfaces.rest.evento;

import com.playzone.pems.application.evento.dto.command.CrearReservaPublicaCommand;
import com.playzone.pems.application.evento.dto.command.ReprogramarReservaCommand;
import com.playzone.pems.application.evento.dto.query.MetricasReservaQuery;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.in.*;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.storage.StoragePort;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.domain.usuario.repository.SedeRepository;
import com.playzone.pems.interfaces.rest.evento.request.CrearReservaRequest;
import com.playzone.pems.interfaces.rest.evento.request.ReprogramarReservaRequest;
import com.playzone.pems.interfaces.rest.evento.response.MetricasReservaResponse;
import com.playzone.pems.interfaces.rest.evento.response.ReservaPublicaResponse;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final ReservaPublicaRepository    reservaRepository;
    private final ClienteRepository           clienteRepository;
    private final SedeRepository              sedeRepository;
    private final StoragePort                 storagePort;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReservaPublicaResponse>>> listar(
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestAttribute("idUsuario") Long idUsuario,
            Pageable pageable) {

        Page<ReservaPublicaQuery> result;
        if (idCliente != null) {
            result = consultarUseCase.consultarPorCliente(idCliente, pageable);
        } else if (idSede != null && fecha != null) {
            result = consultarUseCase.consultarPorSedeYFecha(idSede, fecha, pageable);
        } else if (idSede != null && estado != null) {
            result = consultarUseCase.consultarPorSedeYEstado(idSede, estado, pageable);
        } else {
            result = consultarUseCase.consultarPorCliente(idUsuario, pageable);
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
            @RequestParam(required = false)                    String    medioPago,
            @RequestParam(defaultValue = "0")                  int       page,
            @RequestParam(defaultValue = "20")                 int       size,
            @RequestParam(defaultValue = "fechaEvento,asc")    String    sort) {

        String[]       parts    = sort.split(",");
        Sort.Direction dir      = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                                  ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable       pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

        return ResponseEntity.ok(ApiResponse.ok(
                buscarAdminUseCase.buscar(idSede, estado, fecha, ingresado, esReprogramacion, search, medioPago, pageable)
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

    @GetMapping("/ticket/{numeroTicket}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> obtenerPorTicket(
            @PathVariable String numeroTicket) {

        ReservaPublica r = reservaRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "numeroTicket", numeroTicket));
        return ResponseEntity.ok(ApiResponse.ok(toResponse(buildQueryFromRepo(r))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> obtenerPorId(
            @PathVariable Long id) {

        ReservaPublica r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        return ResponseEntity.ok(ApiResponse.ok(toResponse(buildQueryFromRepo(r))));
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
                        .medioPago(request.getMedioPago())
                        .referenciaPago(request.getReferenciaPago())
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

    @PostMapping("/{id}/confirmar-pago")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> confirmarPago(
            @PathVariable Long id) {

        ReservaPublica reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        if (reserva.getEstado() != EstadoReservaPublica.PENDIENTE) {
            throw new ValidationException("Solo se pueden confirmar reservas en estado PENDIENTE.");
        }
        ReservaPublica guardada = reservaRepository.save(
                reserva.toBuilder().estado(EstadoReservaPublica.CONFIRMADA).build());
        return ResponseEntity.ok(ApiResponse.ok(toResponse(buildQueryFromRepo(guardada))));
    }

    @PostMapping("/{idReserva}/comprobante")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<ApiResponse<ReservaPublicaResponse>> subirComprobante(
            @PathVariable Long idReserva,
            @RequestParam("archivo") MultipartFile archivo) {

        String tipo = archivo.getContentType();
        if (tipo == null || (!tipo.startsWith("image/") && !tipo.equals("application/pdf"))) {
            throw new ValidationException("El archivo debe ser una imagen o PDF.");
        }
        if (archivo.getSize() > 10L * 1024 * 1024) {
            throw new ValidationException("El archivo no puede superar 10 MB.");
        }

        String urlComprobante = storagePort.guardar(archivo, "comprobantes");

        ReservaPublica reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", idReserva));

        ReservaPublica guardada = reservaRepository.save(
                reserva.toBuilder().referenciaPago(urlComprobante).build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(buildQueryFromRepo(guardada))));
    }

    private ReservaPublicaQuery buildQueryFromRepo(ReservaPublica r) {
        String nombreCliente = clienteRepository.findById(r.getIdCliente())
                .map(c -> c.getNombre()).orElse(null);
        String correoCliente = clienteRepository.findById(r.getIdCliente())
                .map(c -> c.getCorreo()).orElse(null);
        String nombreSede = sedeRepository.findById(r.getIdSede())
                .map(s -> s.getNombre()).orElse("Sede Principal");
        return ReservaPublicaQuery.builder()
                .id(r.getId())
                .idCliente(r.getIdCliente())
                .nombreCliente(nombreCliente)
                .correoCliente(correoCliente)
                .idSede(r.getIdSede())
                .nombreSede(nombreSede)
                .estado(r.getEstado().getCodigo())
                .canalReserva(r.getCanalReserva() != null ? r.getCanalReserva().getCodigo() : null)
                .tipoDia(r.getTipoDia() != null ? r.getTipoDia().getCodigo() : null)
                .fechaEvento(r.getFechaEvento())
                .numeroTicket(r.getNumeroTicket())
                .precioHistorico(r.getPrecioHistorico())
                .descuentoAplicado(r.getDescuentoAplicado())
                .totalPagado(r.getTotalPagado())
                .nombreNino(r.getNombreNino())
                .edadNino(r.getEdadNino())
                .nombreAcompanante(r.getNombreAcompanante())
                .dniAcompanante(r.getDniAcompanante())
                .firmoConsentimiento(r.isFirmoConsentimiento())
                .esReprogramacion(r.isEsReprogramacion())
                .vecesReprogramada(r.getVecesReprogramada())
                .ingresado(r.isIngresado())
                .fechaIngreso(r.getFechaIngreso())
                .codigoQr(r.getCodigoQr())
                .medioPago(r.getMedioPago())
                .referenciaPago(r.getReferenciaPago())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }

    private ReservaPublicaResponse toResponse(ReservaPublicaQuery q) {
        return ReservaPublicaResponse.builder()
                .id(q.getId())
                .idCliente(q.getIdCliente())
                .nombreCliente(q.getNombreCliente())
                .correoCliente(q.getCorreoCliente())
                .idSede(q.getIdSede())
                .nombreSede(q.getNombreSede())
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
                .dniAcompanante(q.getDniAcompanante())
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
