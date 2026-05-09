package com.playzone.pems.interfaces.rest.promocion;

import com.playzone.pems.application.promocion.dto.command.CrearPromocionCommand;
import com.playzone.pems.application.promocion.dto.query.PromocionQuery;
import com.playzone.pems.application.promocion.port.in.CrearPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.DesactivarPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.ListarPromocionesUseCase;
import com.playzone.pems.interfaces.rest.promocion.request.CrearPromocionRequest;
import com.playzone.pems.interfaces.rest.promocion.response.PromocionResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promociones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PromocionController {

    private final CrearPromocionUseCase      crearUseCase;
    private final ListarPromocionesUseCase   listarUseCase;
    private final DesactivarPromocionUseCase desactivarUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromocionResponse>>> listar() {
        List<PromocionResponse> lista = listarUseCase.listar()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromocionResponse>> crear(
            @Valid @RequestBody CrearPromocionRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        PromocionQuery query = crearUseCase.ejecutar(CrearPromocionCommand.builder()
                .tipoPromocion(request.getTipoPromocion())
                .idSede(request.getIdSede())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .valorDescuento(request.getValorDescuento())
                .condicion(request.getCondicion())
                .minimoPersonas(request.getMinimoPersonas())
                .soloTipoDia(request.getSoloTipoDia())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .esAutomatica(request.getEsAutomatica())
                .idUsuarioCreador(idUsuarioAdmin)
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @DeleteMapping("/{idPromocion}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long idPromocion) {
        desactivarUseCase.ejecutar(idPromocion);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private PromocionResponse toResponse(PromocionQuery q) {
        return PromocionResponse.builder()
                .id(q.getId())
                .tipoPromocion(q.getTipoPromocion())
                .idSede(q.getIdSede())
                .nombre(q.getNombre())
                .descripcion(q.getDescripcion())
                .valorDescuento(q.getValorDescuento())
                .condicion(q.getCondicion())
                .minimoPersonas(q.getMinimoPersonas())
                .soloTipoDia(q.getSoloTipoDia())
                .fechaInicio(q.getFechaInicio())
                .fechaFin(q.getFechaFin())
                .activo(q.isActivo())
                .esAutomatica(q.isEsAutomatica())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}