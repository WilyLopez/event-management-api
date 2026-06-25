package com.playzone.pems.interfaces.rest.contrato;

import com.playzone.pems.application.contrato.dto.command.ActualizarContratoCommand;
import com.playzone.pems.application.contrato.dto.command.CambiarEstadoContratoCommand;
import com.playzone.pems.application.contrato.dto.command.GenerarContratoCommand;
import com.playzone.pems.application.contrato.dto.query.ContratoPageQuery;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;
import com.playzone.pems.application.contrato.port.in.ActualizarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.CambiarEstadoContratoUseCase;
import com.playzone.pems.application.contrato.port.in.FirmarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.GenerarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.ListarContratosUseCase;
import com.playzone.pems.application.contrato.port.in.ObtenerContratoUseCase;
import com.playzone.pems.application.contrato.port.in.ReemplazarContratoUseCase;
import com.playzone.pems.interfaces.rest.contrato.mapper.ContratoResponseMapper;
import com.playzone.pems.interfaces.rest.contrato.request.ActualizarContratoRequest;
import com.playzone.pems.interfaces.rest.contrato.request.CambiarEstadoRequest;
import com.playzone.pems.interfaces.rest.contrato.request.GenerarContratoRequest;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.interfaces.rest.contrato.response.ContratoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import com.playzone.pems.shared.util.SortUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final GenerarContratoUseCase       generarUseCase;
    private final FirmarContratoUseCase        firmarUseCase;
    private final ObtenerContratoUseCase       obtenerUseCase;
    private final ActualizarContratoUseCase    actualizarUseCase;
    private final ListarContratosUseCase       listarUseCase;
    private final CambiarEstadoContratoUseCase estadoUseCase;
    private final ReemplazarContratoUseCase    reemplazarUseCase;
    private final ContratoResponseMapper       mapper;
    private final SupabaseAuthFacade           supabaseAuthFacade;

    @GetMapping
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<PagedResponse<ContratoResponse>>> listar(
            @RequestParam(defaultValue = "0")                  int       page,
            @RequestParam(defaultValue = "15")                 int       size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String    sort,
            @RequestParam(required = false)                    String    search,
            @RequestParam(required = false)                    String    estado,
            @RequestParam(required = false)                    Long      idSede,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                               LocalDate fechaEvento) {

        Sort parsedSort = SortUtils.parsearSort(sort);
        String sortProperty = "fechaCreacion".equals(sort.split(",")[0]) ? "createdAt" : sort.split(",")[0];
        Sort finalSort = Sort.by(parsedSort.iterator().next().getDirection(), sortProperty);

        ContratoPageQuery resultado = listarUseCase.ejecutar(
                search, estado, idSede, fechaEvento,
                PageRequest.of(page, size, finalSort));

        PagedResponse<ContratoResponse> paginado = PagedResponse.<ContratoResponse>builder()
                .content(resultado.getContent().stream().map(mapper::toResponse).toList())
                .page(resultado.getPage())
                .size(resultado.getSize())
                .totalElements(resultado.getTotalElements())
                .totalPages(resultado.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(paginado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<ContratoResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(obtenerUseCase.porId(id))));
    }

    @GetMapping("/eventos/{idEvento}")
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<ContratoResponse>> obtenerPorEvento(
            @PathVariable Long idEvento) {
        return ResponseEntity.ok(ApiResponse.ok(
                mapper.toResponse(obtenerUseCase.porEvento(idEvento))));
    }

    @PostMapping("/eventos/{idEvento}")
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<ContratoResponse>> generar(
            @PathVariable Long idEvento,
            @RequestBody(required = false) GenerarContratoRequest request) {

        String contenido = request != null ? request.getContenidoTexto() : null;
        String plantilla = request != null ? request.getPlantilla() : null;

        ContratoQuery query = generarUseCase.ejecutar(GenerarContratoCommand.builder()
                .idEventoPrivado(idEvento)
                .idUsuarioRedactor(usuarioActual())
                .contenidoTexto(contenido)
                .plantilla(plantilla)
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(mapper.toResponse(query)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<ContratoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarContratoRequest request) {

        ContratoQuery query = actualizarUseCase.ejecutar(ActualizarContratoCommand.builder()
                .id(id)
                .contenidoTexto(request.getContenidoTexto())
                .plantilla(request.getPlantilla())
                .observaciones(request.getObservaciones())
                .idUsuario(usuarioActual())
                .build());

        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(query)));
    }

    @PostMapping("/{idContrato}/firmar")
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<ContratoResponse>> firmar(
            @PathVariable Long idContrato) {
        return ResponseEntity.ok(ApiResponse.ok(
                mapper.toResponse(firmarUseCase.ejecutar(idContrato, usuarioActual()))));
    }

    @PostMapping("/{idContrato}/estado")
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<ContratoResponse>> cambiarEstado(
            @PathVariable Long idContrato,
            @Valid @RequestBody CambiarEstadoRequest request) {

        ContratoQuery query = estadoUseCase.ejecutar(CambiarEstadoContratoCommand.builder()
                .idContrato(idContrato)
                .nuevoEstado(request.getNuevoEstado())
                .motivo(request.getMotivo())
                .idUsuarioAdmin(usuarioActual())
                .build());

        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(query)));
    }

    @PostMapping("/{idContrato}/reemplazar")
    @PreAuthorize("hasAuthority('evento.contrato')")
    public ResponseEntity<ApiResponse<ContratoResponse>> reemplazar(
            @PathVariable Long idContrato) {

        ContratoQuery query = reemplazarUseCase.reemplazar(idContrato, usuarioActual());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(mapper.toResponse(query)));
    }

    private UUID usuarioActual() {
        return supabaseAuthFacade.usuarioActualId()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
    }
}
