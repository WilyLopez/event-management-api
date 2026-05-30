package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.RegistrarIngresoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.RegistroIngresoQuery;
import com.playzone.pems.application.finanzas.port.in.RegistrarIngresoUseCase;
import com.playzone.pems.interfaces.rest.finanzas.request.RegistrarIngresoManualRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.RegistroIngresoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ingresos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class IngresoController {

    private final RegistrarIngresoUseCase useCase;

    @GetMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<PagedResponse<RegistroIngresoResponse>>> listar(
            @PathVariable Long idSede,
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<RegistroIngresoResponse> body = PagedResponse.of(
                useCase.listar(idSede, pageable).map(this::toResponse));
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/sedes/{idSede}/rango")
    public ResponseEntity<ApiResponse<List<RegistroIngresoResponse>>> listarPorRango(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<RegistroIngresoResponse> body = useCase.listarPorRango(idSede, inicio, fin)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<RegistroIngresoResponse>> registrar(
            @PathVariable Long idSede,
            @Valid @RequestBody RegistrarIngresoManualRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        RegistroIngresoQuery query = useCase.registrar(RegistrarIngresoManualCommand.builder()
                .idTipoIngreso(request.getIdTipoIngreso())
                .idSede(idSede)
                .monto(request.getMonto())
                .fecha(request.getFecha())
                .medioPago(request.getMedioPago())
                .descripcion(request.getDescripcion())
                .idUsuarioRegistra(idUsuarioAdmin)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private RegistroIngresoResponse toResponse(RegistroIngresoQuery q) {
        return RegistroIngresoResponse.builder()
                .id(q.getId())
                .idTipoIngreso(q.getIdTipoIngreso())
                .nombreTipoIngreso(q.getNombreTipoIngreso())
                .categoriaIngreso(q.getCategoriaIngreso())
                .idSede(q.getIdSede())
                .idReservaPublica(q.getIdReservaPublica())
                .idEventoPrivado(q.getIdEventoPrivado())
                .monto(q.getMonto())
                .fecha(q.getFecha())
                .medioPago(q.getMedioPago())
                .descripcion(q.getDescripcion())
                .esAutomatico(q.isEsAutomatico())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}
