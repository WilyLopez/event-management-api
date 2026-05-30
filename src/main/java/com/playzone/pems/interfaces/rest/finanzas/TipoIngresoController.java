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
@PreAuthorize("hasRole('ADMIN')")
public class TipoIngresoController {

    private final GestionarTipoIngresoUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoIngresoResponse>>> listar() {
        List<TipoIngresoResponse> body = useCase.listar().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TipoIngresoResponse>> crear(
            @Valid @RequestBody CrearTipoIngresoRequest request) {
        TipoIngresoQuery query = useCase.crear(CrearTipoIngresoCommand.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        useCase.desactivar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private TipoIngresoResponse toResponse(TipoIngresoQuery q) {
        return TipoIngresoResponse.builder()
                .id(q.getId())
                .nombre(q.getNombre())
                .descripcion(q.getDescripcion())
                .categoria(q.getCategoria())
                .activo(q.isActivo())
                .build();
    }
}
