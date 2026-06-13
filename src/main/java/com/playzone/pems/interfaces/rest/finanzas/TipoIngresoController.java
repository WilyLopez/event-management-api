package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.CrearTipoIngresoCommand;
import com.playzone.pems.application.finanzas.dto.query.TipoIngresoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarTipoIngresoUseCase;
import com.playzone.pems.interfaces.rest.finanzas.request.CrearTipoIngresoRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.TipoIngresoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-ingreso")
@RequiredArgsConstructor
public class TipoIngresoController {

    private final GestionarTipoIngresoUseCase useCase;

    @GetMapping
    @PreAuthorize("hasAuthority('ingreso.ver')")
    public ResponseEntity<ApiResponse<List<TipoIngresoResponse>>> listar() {
        List<TipoIngresoResponse> body = useCase.listar().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('catalogo.editar')")
    public ResponseEntity<ApiResponse<TipoIngresoResponse>> crear(
            @Valid @RequestBody CrearTipoIngresoRequest request) {
        TipoIngresoQuery query = useCase.crear(CrearTipoIngresoCommand.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasAuthority('catalogo.editar')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable String codigo) {
        useCase.desactivar(codigo);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private TipoIngresoResponse toResponse(TipoIngresoQuery q) {
        return TipoIngresoResponse.builder()
                .codigo(q.getCodigo())
                .nombre(q.getNombre())
                .descripcion(q.getDescripcion())
                .esSistema(q.isEsSistema())
                .orden(q.getOrden())
                .activo(q.isActivo())
                .build();
    }
}
