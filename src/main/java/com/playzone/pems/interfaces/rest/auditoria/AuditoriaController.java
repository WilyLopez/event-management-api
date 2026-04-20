package com.playzone.pems.interfaces.rest.auditoria;

import com.playzone.pems.domain.auditoria.repository.LogAuditoriaRepository;
import com.playzone.pems.interfaces.rest.auditoria.response.LogAuditoriaResponse;
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
    public ResponseEntity<ApiResponse<PagedResponse<LogAuditoriaResponse>>> listarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "0")  int pagina,
            @RequestParam(defaultValue = "20") int tamano) {

        Page<LogAuditoriaResponse> page = logRepository
                .findByFechasBetween(desde, hasta, PaginacionUtil.construir(pagina, tamano, "timestamp", "desc"))
                .map(this::toResponse);

        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(page)));
    }

    @GetMapping("/usuarios/{idAdmin}")
    public ResponseEntity<ApiResponse<PagedResponse<LogAuditoriaResponse>>> listarPorUsuario(
            @PathVariable Long idAdmin,
            @RequestParam(defaultValue = "0")  int pagina,
            @RequestParam(defaultValue = "20") int tamano) {

        Page<LogAuditoriaResponse> page = logRepository
                .findByUsuario(idAdmin, PaginacionUtil.construir(pagina, tamano, "timestamp", "desc"))
                .map(this::toResponse);

        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.of(page)));
    }

    private LogAuditoriaResponse toResponse(com.playzone.pems.domain.auditoria.model.LogAuditoria log) {
        return LogAuditoriaResponse.builder()
                .id(log.getId())
                .idUsuarioAdmin(log.getIdUsuarioAdmin())
                .accion(log.getAccion())
                .modulo(log.getModulo())
                .entidadAfectada(log.getEntidadAfectada())
                .idEntidad(log.getIdEntidad())
                .descripcion(log.getDescripcion())
                .ipOrigen(log.getIpOrigen())
                .timestamp(log.getTimestamp())
                .build();
    }
}