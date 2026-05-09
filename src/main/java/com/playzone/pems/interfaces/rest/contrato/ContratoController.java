package com.playzone.pems.interfaces.rest.contrato;

import com.playzone.pems.application.contrato.dto.command.ActualizarContratoCommand;
import com.playzone.pems.application.contrato.dto.command.CambiarEstadoContratoCommand;
import com.playzone.pems.application.contrato.dto.command.GenerarContratoCommand;
import com.playzone.pems.application.contrato.dto.query.ActividadContratoQuery;
import com.playzone.pems.application.contrato.dto.query.ContratoPageQuery;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;
import com.playzone.pems.application.contrato.dto.query.DocumentoContratoQuery;
import com.playzone.pems.application.contrato.port.in.ActualizarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.CambiarEstadoContratoUseCase;
import com.playzone.pems.application.contrato.port.in.FirmarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.GenerarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.ListarContratosUseCase;
import com.playzone.pems.application.contrato.port.in.ObtenerContratoUseCase;
import com.playzone.pems.interfaces.rest.contrato.request.ActualizarContratoRequest;
import com.playzone.pems.interfaces.rest.contrato.request.CambiarEstadoRequest;
import com.playzone.pems.interfaces.rest.contrato.request.GenerarContratoRequest;
import com.playzone.pems.interfaces.rest.contrato.response.ContratoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contratos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ContratoController {

    private final GenerarContratoUseCase       generarUseCase;
    private final FirmarContratoUseCase        firmarUseCase;
    private final ObtenerContratoUseCase       obtenerUseCase;
    private final ActualizarContratoUseCase    actualizarUseCase;
    private final ListarContratosUseCase       listarUseCase;
    private final CambiarEstadoContratoUseCase estadoUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ContratoResponse>>> listar(
            @RequestParam(defaultValue = "0")                  int     page,
            @RequestParam(defaultValue = "15")                 int     size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String  sort,
            @RequestParam(required = false)                    String  search,
            @RequestParam(required = false)                    String  estado,
            @RequestParam(required = false)                    Long    idSede) {

        String[]       parts = sort.split(",");
        Sort.Direction dir   = parts.length > 1 && "asc".equalsIgnoreCase(parts[1])
                               ? Sort.Direction.ASC : Sort.Direction.DESC;

        ContratoPageQuery resultado = listarUseCase.ejecutar(
                search, estado, idSede,
                PageRequest.of(page, size, Sort.by(dir, parts[0])));

        PagedResponse<ContratoResponse> paginado = PagedResponse.<ContratoResponse>builder()
                .content(resultado.getContent().stream().map(this::toResponse).toList())
                .page(resultado.getPage())
                .size(resultado.getSize())
                .totalElements(resultado.getTotalElements())
                .totalPages(resultado.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(paginado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContratoResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(obtenerUseCase.porId(id))));
    }

    @GetMapping("/eventos/{idEvento}")
    public ResponseEntity<ApiResponse<ContratoResponse>> obtenerPorEvento(
            @PathVariable Long idEvento) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(obtenerUseCase.porEvento(idEvento))));
    }

    @PostMapping("/eventos/{idEvento}")
    public ResponseEntity<ApiResponse<ContratoResponse>> generar(
            @PathVariable Long idEvento,
            @Valid @RequestBody GenerarContratoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        ContratoQuery query = generarUseCase.ejecutar(GenerarContratoCommand.builder()
                .idEventoPrivado(idEvento)
                .idUsuarioRedactor(idUsuarioAdmin)
                .contenidoTexto(request.getContenidoTexto())
                .plantilla(request.getPlantilla())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContratoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarContratoRequest request) {

        ContratoQuery query = actualizarUseCase.ejecutar(ActualizarContratoCommand.builder()
                .id(id)
                .contenidoTexto(request.getContenidoTexto())
                .plantilla(request.getPlantilla())
                .observaciones(request.getObservaciones())
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @PostMapping("/{idContrato}/firmar")
    public ResponseEntity<ApiResponse<ContratoResponse>> firmar(
            @PathVariable Long idContrato) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(firmarUseCase.ejecutar(idContrato))));
    }

    @PostMapping("/{idContrato}/estado")
    public ResponseEntity<ApiResponse<ContratoResponse>> cambiarEstado(
            @PathVariable Long idContrato,
            @Valid @RequestBody CambiarEstadoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        ContratoQuery query = estadoUseCase.ejecutar(CambiarEstadoContratoCommand.builder()
                .idContrato(idContrato)
                .nuevoEstado(request.getNuevoEstado())
                .motivo(request.getMotivo())
                .idUsuarioAdmin(idUsuarioAdmin)
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    private ContratoResponse toResponse(ContratoQuery q) {
        return ContratoResponse.builder()
                .id(q.getId())
                .idEventoPrivado(q.getIdEventoPrivado())
                .estado(q.getEstado())
                .contenidoTexto(q.getContenidoTexto())
                .archivoPdfUrl(q.getArchivoPdfUrl())
                .fechaFirma(q.getFechaFirma())
                .usuarioRedactor(q.getUsuarioRedactor())
                .plantilla(q.getPlantilla())
                .observaciones(q.getObservaciones())
                .version(q.getVersion())
                .nombreCliente(q.getNombreCliente())
                .correoCliente(q.getCorreoCliente())
                .tipoEvento(q.getTipoEvento())
                .fechaEvento(q.getFechaEvento())
                .turno(q.getTurno())
                .aforoDeclarado(q.getAforoDeclarado())
                .precioTotalContrato(q.getPrecioTotalContrato())
                .montoAdelanto(q.getMontoAdelanto())
                .saldoPendiente(q.getSaldoPendiente())
                .documentos(q.getDocumentos() == null ? null : q.getDocumentos().stream()
                        .map(d -> ContratoResponse.DocumentoContratoResponse.builder()
                                .id(d.getId())
                                .nombre(d.getNombre())
                                .archivoUrl(d.getArchivoUrl())
                                .tipoArchivo(d.getTipoArchivo())
                                .tamanobytes(d.getTamanobytes())
                                .usuarioCarga(d.getUsuarioCarga())
                                .fechaCarga(d.getFechaCarga())
                                .build()).toList())
                .actividades(q.getActividades() == null ? null : q.getActividades().stream()
                        .map(a -> ContratoResponse.ActividadContratoResponse.builder()
                                .id(a.getId())
                                .accion(a.getAccion())
                                .descripcion(a.getDescripcion())
                                .usuario(a.getUsuario())
                                .fechaAccion(a.getFechaAccion())
                                .build()).toList())
                .fechaCreacion(q.getFechaCreacion())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}