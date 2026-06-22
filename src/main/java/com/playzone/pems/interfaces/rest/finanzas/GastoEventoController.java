package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.RegistrarGastoEventoCommand;
import com.playzone.pems.application.finanzas.dto.query.GastoEventoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarGastoEventoUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.interfaces.rest.finanzas.request.RegistrarGastoEventoRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.GastoEventoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos-privados/{idEvento}/gastos")
@RequiredArgsConstructor
public class GastoEventoController {

    private final GestionarGastoEventoUseCase useCase;
    private final SupabaseAuthFacade          supabaseAuthFacade;

    @GetMapping
    @PreAuthorize("hasAuthority('egreso.ver')")
    public ResponseEntity<ApiResponse<List<GastoEventoResponse>>> listar(@PathVariable Long idEvento) {
        List<GastoEventoResponse> body = useCase.listarPorEvento(idEvento)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('egreso.crear')")
    public ResponseEntity<ApiResponse<GastoEventoResponse>> registrar(
            @PathVariable Long idEvento,
            @Valid @RequestBody RegistrarGastoEventoRequest request) {
        GastoEventoQuery query = useCase.registrar(RegistrarGastoEventoCommand.builder()
                .idEventoPrivado(idEvento)
                .descripcion(request.getDescripcion())
                .monto(request.getMonto())
                .comprobanteUrl(request.getComprobanteUrl())
                .idUsuarioRegistra(supabaseAuthFacade.usuarioActualId().orElseThrow())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @DeleteMapping("/{idGasto}")
    @PreAuthorize("hasAuthority('egreso.eliminar')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idGasto) {
        useCase.eliminar(idGasto);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private GastoEventoResponse toResponse(GastoEventoQuery q) {
        return GastoEventoResponse.builder()
                .id(q.getId())
                .idEventoPrivado(q.getIdEventoPrivado())
                .descripcion(q.getDescripcion())
                .monto(q.getMonto())
                .comprobanteUrl(q.getComprobanteUrl())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}
