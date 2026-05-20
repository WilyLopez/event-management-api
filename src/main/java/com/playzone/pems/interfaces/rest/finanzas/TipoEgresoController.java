package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.CrearTipoEgresoCommand;
import com.playzone.pems.application.finanzas.dto.query.TipoEgresoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarTipoEgresoUseCase;
import com.playzone.pems.interfaces.rest.finanzas.request.CrearTipoEgresoRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.TipoEgresoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-egreso")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TipoEgresoController {

    private final GestionarTipoEgresoUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoEgresoResponse>>> listar() {
        List<TipoEgresoResponse> body = useCase.listar().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiResponse<List<TipoEgresoResponse>>> listarPorCategoria(
            @PathVariable String categoria) {
        List<TipoEgresoResponse> body = useCase.listarPorCategoria(categoria).stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TipoEgresoResponse>> crear(
            @Valid @RequestBody CrearTipoEgresoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        TipoEgresoQuery query = useCase.crear(CrearTipoEgresoCommand.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .idUsuarioCreador(idUsuarioAdmin)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        useCase.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private TipoEgresoResponse toResponse(TipoEgresoQuery q) {
        return TipoEgresoResponse.builder()
                .id(q.getId())
                .nombre(q.getNombre())
                .descripcion(q.getDescripcion())
                .categoria(q.getCategoria())
                .activo(q.isActivo())
                .build();
    }
}
