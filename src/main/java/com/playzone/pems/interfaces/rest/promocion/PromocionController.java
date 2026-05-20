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
    @PreAuthorize("hasRole('ADMIN')")
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
                .imagenUrl(request.getImagenUrl())
                .bannerUrl(request.getBannerUrl())
                .colorDestacado(request.getColorDestacado())
                .prioridad(request.getPrioridad())
                .textoPublicitario(request.getTextoPublicitario())
                .textoBoton(request.getTextoBoton())
                .urlBoton(request.getUrlBoton())
                .mostrarEnInicio(request.getMostrarEnInicio())
                .mostrarEnCarrusel(request.getMostrarEnCarrusel())
                .mostrarEnPaginaPromociones(request.getMostrarEnPaginaPromociones())
                .mostrarEnCheckout(request.getMostrarEnCheckout())
                .mostrarDestacado(request.getMostrarDestacado())
                .soloMovil(request.getSoloMovil())
                .limiteUsos(request.getLimiteUsos())
                .limitePorCliente(request.getLimitePorCliente())
                .minimoAsistentes(request.getMinimoAsistentes())
                .montoMinimo(request.getMontoMinimo())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{idPromocion}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromocionResponse>> actualizar(
            @PathVariable Long idPromocion,
            @Valid @RequestBody CrearPromocionRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        PromocionQuery query = crearUseCase.ejecutar(CrearPromocionCommand.builder()
                .id(idPromocion)
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
                .imagenUrl(request.getImagenUrl())
                .bannerUrl(request.getBannerUrl())
                .colorDestacado(request.getColorDestacado())
                .prioridad(request.getPrioridad())
                .textoPublicitario(request.getTextoPublicitario())
                .textoBoton(request.getTextoBoton())
                .urlBoton(request.getUrlBoton())
                .mostrarEnInicio(request.getMostrarEnInicio())
                .mostrarEnCarrusel(request.getMostrarEnCarrusel())
                .mostrarEnPaginaPromociones(request.getMostrarEnPaginaPromociones())
                .mostrarEnCheckout(request.getMostrarEnCheckout())
                .mostrarDestacado(request.getMostrarDestacado())
                .soloMovil(request.getSoloMovil())
                .limiteUsos(request.getLimiteUsos())
                .limitePorCliente(request.getLimitePorCliente())
                .minimoAsistentes(request.getMinimoAsistentes())
                .montoMinimo(request.getMontoMinimo())
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
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
                .imagenUrl(q.getImagenUrl())
                .bannerUrl(q.getBannerUrl())
                .colorDestacado(q.getColorDestacado())
                .prioridad(q.getPrioridad())
                .textoPublicitario(q.getTextoPublicitario())
                .textoBoton(q.getTextoBoton())
                .urlBoton(q.getUrlBoton())
                .mostrarEnInicio(q.isMostrarEnInicio())
                .mostrarEnCarrusel(q.isMostrarEnCarrusel())
                .mostrarEnPaginaPromociones(q.isMostrarEnPaginaPromociones())
                .mostrarEnCheckout(q.isMostrarEnCheckout())
                .mostrarDestacado(q.isMostrarDestacado())
                .soloMovil(q.isSoloMovil())
                .limiteUsos(q.getLimiteUsos())
                .limitePorCliente(q.getLimitePorCliente())
                .minimoAsistentes(q.getMinimoAsistentes())
                .montoMinimo(q.getMontoMinimo())
                .vecesUsado(q.getVecesUsado())
                .montoAhorrado(q.getMontoAhorrado())
                .clientesAtraidos(q.getClientesAtraidos())
                .build();
    }
}