package com.playzone.pems.interfaces.rest.auditoria;

import com.playzone.pems.domain.auditoria.model.LogAuditoria;
import com.playzone.pems.domain.auditoria.repository.LogAuditoriaRepository;
import com.playzone.pems.interfaces.rest.auditoria.response.LogAuditoriaResponse;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import com.playzone.pems.shared.util.PaginacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auditoria")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final LogAuditoriaRepository logRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<LogAuditoriaResponse>>> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(required = false) Long   idUsuario,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String entidad,
            @RequestParam(defaultValue = "0")  int pagina,
            @RequestParam(defaultValue = "20") int tamano) {

        boolean hayFiltros = idUsuario != null || modulo != null || accion != null || entidad != null;

        Page<LogAuditoriaResponse> page;
        if (hayFiltros) {
            page = logRepository
                    .findByFiltros(desde, hasta, idUsuario, modulo, accion, entidad,
                            PaginacionUtil.construir(pagina, tamano, "fechaLog", "desc"))
                    .map(this::toResponse);
        } else {
            page = logRepository
                    .findByFechasBetween(desde, hasta, PaginacionUtil.construir(pagina, tamano, "fechaLog", "desc"))
                    .map(this::toResponse);
        }

        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LogAuditoriaResponse>> obtener(@PathVariable Long id) {
        LogAuditoria log = logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LogAuditoria", id));
        return ResponseEntity.ok(ApiResponse.ok(toResponse(log)));
    }

    @GetMapping("/usuarios/{idAdmin}")
    public ResponseEntity<ApiResponse<PagedResponse<LogAuditoriaResponse>>> listarPorUsuario(
            @PathVariable Long idAdmin,
            @RequestParam(defaultValue = "0")  int pagina,
            @RequestParam(defaultValue = "20") int tamano) {

        Page<LogAuditoriaResponse> page = logRepository
                .findByUsuario(idAdmin, PaginacionUtil.construir(pagina, tamano, "fechaLog", "desc"))
                .map(this::toResponse);

        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(page)));
    }

    private LogAuditoriaResponse toResponse(LogAuditoria log) {
        return LogAuditoriaResponse.builder()
                .id(log.getId())
                .idUsuarioAdmin(log.getIdUsuarioAdmin())
                .nombreUsuario(log.getNombreUsuario())
                .accion(log.getAccion())
                .modulo(log.getModulo())
                .entidadAfectada(log.getEntidadAfectada())
                .idEntidad(log.getIdEntidad())
                .valorAnterior(log.getValorAnterior())
                .valorNuevo(log.getValorNuevo())
                .descripcion(log.getDescripcion())
                .ipOrigen(log.getIpOrigen())
                .userAgent(log.getUserAgent())
                .nivel(log.getNivel())
                .resultado(log.getResultado())
                .fechaLog(log.getFechaLog())
                .build();
    }
}
