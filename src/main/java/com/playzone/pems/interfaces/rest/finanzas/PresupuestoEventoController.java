package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.GuardarPresupuestoCommand;
import com.playzone.pems.application.finanzas.dto.query.PresupuestoEventoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarPresupuestoEventoUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.interfaces.rest.finanzas.request.EjecutarPresupuestoRequest;
import com.playzone.pems.interfaces.rest.finanzas.request.GuardarPresupuestoRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.PresupuestoEventoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/presupuesto-eventos")
@RequiredArgsConstructor
public class PresupuestoEventoController {

    private final GestionarPresupuestoEventoUseCase useCase;
    private final SupabaseAuthFacade                supabaseAuthFacade;

    @GetMapping("/eventos/{idEvento}")
    @PreAuthorize("hasAuthority('egreso.ver')")
    public ResponseEntity<ApiResponse<List<PresupuestoEventoResponse>>> listarPorEvento(
            @PathVariable Long idEvento) {
        List<PresupuestoEventoResponse> body = useCase.listarPorEvento(idEvento)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping("/eventos/{idEvento}")
    @PreAuthorize("hasAuthority('egreso.crear')")
    public ResponseEntity<ApiResponse<PresupuestoEventoResponse>> guardar(
            @PathVariable Long idEvento,
            @Valid @RequestBody GuardarPresupuestoRequest request) {
        PresupuestoEventoQuery query = useCase.guardar(GuardarPresupuestoCommand.builder()
                .idEventoPrivado(idEvento)
                .concepto(request.getConcepto())
                .categoria(request.getCategoria())
                .montoEstimado(request.getMontoEstimado())
                .idUsuarioRegistra(supabaseAuthFacade.usuarioActualId().orElseThrow())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{id}/ejecutar")
    @PreAuthorize("hasAuthority('egreso.editar')")
    public ResponseEntity<ApiResponse<PresupuestoEventoResponse>> marcarEjecutado(
            @PathVariable Long id,
            @Valid @RequestBody EjecutarPresupuestoRequest request) {
        PresupuestoEventoQuery query = useCase.marcarEjecutado(id, request.getMontoReal());
        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('egreso.eliminar')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private PresupuestoEventoResponse toResponse(PresupuestoEventoQuery q) {
        return PresupuestoEventoResponse.builder()
                .id(q.getId())
                .idEventoPrivado(q.getIdEventoPrivado())
                .concepto(q.getConcepto())
                .categoria(q.getCategoria())
                .montoEstimado(q.getMontoEstimado())
                .montoReal(q.getMontoReal())
                .estado(q.getEstado())
                .fechaCreacion(q.getFechaCreacion())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}
