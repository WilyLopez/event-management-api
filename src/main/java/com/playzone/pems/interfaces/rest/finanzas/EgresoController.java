package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.ActualizarEgresoCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarEgresoCommand;
import com.playzone.pems.application.finanzas.dto.query.RegistroEgresoQuery;
import com.playzone.pems.application.finanzas.port.in.RegistrarEgresoUseCase;
import com.playzone.pems.interfaces.rest.finanzas.request.RegistrarEgresoRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.RegistroEgresoResponse;
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
@RequestMapping("/api/v1/egresos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EgresoController {

    private final RegistrarEgresoUseCase useCase;

    @GetMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<PagedResponse<RegistroEgresoResponse>>> listar(
            @PathVariable Long idSede,
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<RegistroEgresoResponse> body = PagedResponse.of(
                useCase.listar(idSede, pageable).map(this::toResponse));
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/sedes/{idSede}/periodo")
    public ResponseEntity<ApiResponse<List<RegistroEgresoResponse>>> listarPorPeriodo(
            @PathVariable Long idSede,
            @RequestParam int anio,
            @RequestParam int mes) {
        List<RegistroEgresoResponse> body = useCase.listarPorPeriodo(idSede, anio, mes)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/sedes/{idSede}/rango")
    public ResponseEntity<ApiResponse<List<RegistroEgresoResponse>>> listarPorRango(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<RegistroEgresoResponse> body = useCase.listarPorRango(idSede, inicio, fin)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<RegistroEgresoResponse>> registrar(
            @PathVariable Long idSede,
            @Valid @RequestBody RegistrarEgresoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        RegistroEgresoQuery query = useCase.registrar(RegistrarEgresoCommand.builder()
                .idTipoEgreso(request.getIdTipoEgreso())
                .idSede(idSede)
                .monto(request.getMonto())
                .fecha(request.getFecha())
                .periodoAnio(request.getPeriodoAnio())
                .periodoMes(request.getPeriodoMes())
                .descripcion(request.getDescripcion())
                .comprobanteUrl(request.getComprobanteUrl())
                .esRecurrente(request.isEsRecurrente())
                .idUsuarioRegistra(idUsuarioAdmin)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistroEgresoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarEgresoRequest request) {
        RegistroEgresoQuery query = useCase.actualizar(ActualizarEgresoCommand.builder()
                .id(id)
                .idTipoEgreso(request.getIdTipoEgreso())
                .monto(request.getMonto())
                .fecha(request.getFecha())
                .periodoAnio(request.getPeriodoAnio())
                .periodoMes(request.getPeriodoMes())
                .descripcion(request.getDescripcion())
                .comprobanteUrl(request.getComprobanteUrl())
                .esRecurrente(request.isEsRecurrente())
                .build());
        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private RegistroEgresoResponse toResponse(RegistroEgresoQuery q) {
        return RegistroEgresoResponse.builder()
                .id(q.getId())
                .idTipoEgreso(q.getIdTipoEgreso())
                .nombreTipoEgreso(q.getNombreTipoEgreso())
                .categoriaEgreso(q.getCategoriaEgreso())
                .idSede(q.getIdSede())
                .monto(q.getMonto())
                .fecha(q.getFecha())
                .periodoAnio(q.getPeriodoAnio())
                .periodoMes(q.getPeriodoMes())
                .descripcion(q.getDescripcion())
                .comprobanteUrl(q.getComprobanteUrl())
                .esRecurrente(q.isEsRecurrente())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}
