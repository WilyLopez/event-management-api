package com.playzone.pems.interfaces.rest.promocion;

import com.playzone.pems.application.promocion.dto.command.CrearPromocionCommand;
import com.playzone.pems.application.promocion.dto.query.PromocionQuery;
import com.playzone.pems.application.promocion.port.in.CrearPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.DesactivarPromocionUseCase;
import com.playzone.pems.application.promocion.port.in.ListarPromocionesUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
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
public class PromocionController {

    private final CrearPromocionUseCase      crearUseCase;
    private final ListarPromocionesUseCase   listarUseCase;
    private final DesactivarPromocionUseCase desactivarUseCase;
    private final SupabaseAuthFacade         supabaseAuthFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromocionResponse>>> listar() {
        List<PromocionResponse> lista = listarUseCase.listar()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/publicas")
    public ResponseEntity<ApiResponse<List<PromocionResponse>>> listarPublicas() {
        List<PromocionResponse> lista = listarUseCase.listar()
                .stream()
                .filter(PromocionQuery::isActivo)
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('promocion.gestionar')")
    public ResponseEntity<ApiResponse<PromocionResponse>> crear(
            @Valid @RequestBody CrearPromocionRequest request) {

        PromocionQuery query = crearUseCase.ejecutar(buildCommand(null, request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{idPromocion}")
    @PreAuthorize("hasAuthority('promocion.gestionar')")
    public ResponseEntity<ApiResponse<PromocionResponse>> actualizar(
            @PathVariable Long idPromocion,
            @Valid @RequestBody CrearPromocionRequest request) {

        PromocionQuery query = crearUseCase.ejecutar(buildCommand(idPromocion, request));
        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @DeleteMapping("/{idPromocion}")
    @PreAuthorize("hasAuthority('promocion.gestionar')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long idPromocion) {
        desactivarUseCase.ejecutar(idPromocion);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private CrearPromocionCommand buildCommand(Long id, CrearPromocionRequest r) {
        return CrearPromocionCommand.builder()
                .id(id)
                .tipoPromocion(r.getTipoPromocion())
                .idSede(r.getIdSede())
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .valorDescuento(r.getValorDescuento())
                .minimoPersonas(r.getMinimoPersonas())
                .soloTipoDia(r.getSoloTipoDia())
                .fechaInicio(r.getFechaInicio())
                .fechaFin(r.getFechaFin())
                .esAutomatica(r.getEsAutomatica())
                .idUsuarioCreador(supabaseAuthFacade.usuarioActualId()
                        .orElseThrow(() -> new IllegalStateException("Sin usuario autenticado en contexto")))
                .prioridad(r.getPrioridad())
                .limiteUsos(r.getLimiteUsos())
                .limitePorCliente(r.getLimitePorCliente())
                .montoMinimo(r.getMontoMinimo())
                .imagenUrl(r.getImagenUrl())
                .bannerUrl(r.getBannerUrl())
                .colorDestacado(r.getColorDestacado())
                .textoPublicitario(r.getTextoPublicitario())
                .textoBoton(r.getTextoBoton())
                .urlBoton(r.getUrlBoton())
                .mostrarEnInicio(r.getMostrarEnInicio())
                .mostrarEnCarrusel(r.getMostrarEnCarrusel())
                .mostrarEnPaginaPromociones(r.getMostrarEnPaginaPromociones())
                .mostrarEnCheckout(r.getMostrarEnCheckout())
                .soloMovil(r.getSoloMovil())
                .build();
    }

    private PromocionResponse toResponse(PromocionQuery q) {
        return PromocionResponse.builder()
                .id(q.getId())
                .tipoPromocion(q.getTipoPromocion())
                .idSede(q.getIdSede())
                .nombre(q.getNombre())
                .descripcion(q.getDescripcion())
                .valorDescuento(q.getValorDescuento())
                .minimoPersonas(q.getMinimoPersonas())
                .soloTipoDia(q.getSoloTipoDia())
                .fechaInicio(q.getFechaInicio())
                .fechaFin(q.getFechaFin())
                .activo(q.isActivo())
                .esAutomatica(q.isEsAutomatica())
                .fechaCreacion(q.getFechaCreacion())
                .prioridad(q.getPrioridad())
                .limiteUsos(q.getLimiteUsos())
                .limitePorCliente(q.getLimitePorCliente())
                .montoMinimo(q.getMontoMinimo())
                .imagenUrl(q.getImagenUrl())
                .bannerUrl(q.getBannerUrl())
                .colorDestacado(q.getColorDestacado())
                .textoPublicitario(q.getTextoPublicitario())
                .textoBoton(q.getTextoBoton())
                .urlBoton(q.getUrlBoton())
                .mostrarEnInicio(q.isMostrarEnInicio())
                .mostrarEnCarrusel(q.isMostrarEnCarrusel())
                .mostrarEnPaginaPromociones(q.isMostrarEnPaginaPromociones())
                .mostrarEnCheckout(q.isMostrarEnCheckout())
                .soloMovil(q.isSoloMovil())
                .vecesUsado(q.getVecesUsado())
                .montoAhorrado(q.getMontoAhorrado())
                .clientesAtraidos(q.getClientesAtraidos())
                .build();
    }
}
