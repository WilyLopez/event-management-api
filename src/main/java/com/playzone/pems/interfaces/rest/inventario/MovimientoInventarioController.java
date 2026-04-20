package com.playzone.pems.interfaces.rest.inventario;

import com.playzone.pems.application.inventario.dto.command.MovimientoInventarioCommand;
import com.playzone.pems.application.inventario.dto.query.ProductoQuery;
import com.playzone.pems.application.inventario.port.in.AjustarStockUseCase;
import com.playzone.pems.application.inventario.port.in.RegistrarEntradaStockUseCase;
import com.playzone.pems.application.inventario.port.in.RegistrarSalidaStockUseCase;
import com.playzone.pems.interfaces.rest.inventario.request.MovimientoInventarioRequest;
import com.playzone.pems.interfaces.rest.inventario.response.ProductoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventario/productos/{idProducto}")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MovimientoInventarioController {

    private final RegistrarEntradaStockUseCase entradaUseCase;
    private final RegistrarSalidaStockUseCase  salidaUseCase;
    private final AjustarStockUseCase          ajusteUseCase;

    @PostMapping("/entrada")
    public ResponseEntity<ApiResponse<ProductoResponse>> entrada(
            @PathVariable Long idProducto,
            @Valid @RequestBody MovimientoInventarioRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(entradaUseCase.ejecutar(buildCommand(idProducto, request, idUsuarioAdmin)))));
    }

    @PostMapping("/salida")
    public ResponseEntity<ApiResponse<ProductoResponse>> salida(
            @PathVariable Long idProducto,
            @Valid @RequestBody MovimientoInventarioRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(salidaUseCase.ejecutar(buildCommand(idProducto, request, idUsuarioAdmin)))));
    }

    @PostMapping("/ajuste")
    public ResponseEntity<ApiResponse<ProductoResponse>> ajuste(
            @PathVariable Long idProducto,
            @Valid @RequestBody MovimientoInventarioRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(ajusteUseCase.ejecutar(buildCommand(idProducto, request, idUsuarioAdmin)))));
    }

    private MovimientoInventarioCommand buildCommand(Long idProducto,
                                                     MovimientoInventarioRequest r,
                                                     Long idUsuario) {
        return MovimientoInventarioCommand.builder()
                .idProducto(idProducto)
                .tipoMovimiento(r.getTipoMovimiento())
                .cantidad(r.getCantidad())
                .motivo(r.getMotivo())
                .idUsuario(idUsuario)
                .build();
    }

    private ProductoResponse toResponse(ProductoQuery q) {
        return ProductoResponse.builder()
                .id(q.getId())
                .idSede(q.getIdSede())
                .categoria(q.getCategoria())
                .nombre(q.getNombre())
                .precio(q.getPrecio())
                .stockActual(q.getStockActual())
                .stockMinimo(q.getStockMinimo())
                .unidadMedida(q.getUnidadMedida())
                .activo(q.isActivo())
                .enAlertaDeStock(q.isEnAlertaDeStock())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}