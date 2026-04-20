package com.playzone.pems.interfaces.rest.inventario;

import com.playzone.pems.application.inventario.dto.query.AlertaStockQuery;
import com.playzone.pems.application.inventario.dto.query.ProductoQuery;
import com.playzone.pems.application.inventario.port.in.ConsultarAlertasStockUseCase;
import com.playzone.pems.interfaces.rest.inventario.response.AlertaStockResponse;
import com.playzone.pems.interfaces.rest.inventario.response.ProductoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductoController {

    private final ConsultarAlertasStockUseCase alertasUseCase;

    @GetMapping("/sedes/{idSede}/alertas-stock")
    public ResponseEntity<ApiResponse<List<AlertaStockResponse>>> alertasStock(
            @PathVariable Long idSede) {

        List<AlertaStockResponse> lista = alertasUseCase.ejecutar(idSede)
                .stream().map(this::toAlertaResponse).toList();

        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    private ProductoResponse toProductoResponse(ProductoQuery q) {
        return ProductoResponse.builder()
                .id(q.getId())
                .idSede(q.getIdSede())
                .categoria(q.getCategoria())
                .nombre(q.getNombre())
                .descripcion(q.getDescripcion())
                .precio(q.getPrecio())
                .stockActual(q.getStockActual())
                .stockMinimo(q.getStockMinimo())
                .unidadMedida(q.getUnidadMedida())
                .activo(q.isActivo())
                .enAlertaDeStock(q.isEnAlertaDeStock())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }

    private AlertaStockResponse toAlertaResponse(AlertaStockQuery q) {
        return AlertaStockResponse.builder()
                .idProducto(q.getIdProducto())
                .idSede(q.getIdSede())
                .nombre(q.getNombre())
                .categoria(q.getCategoria())
                .stockActual(q.getStockActual())
                .stockMinimo(q.getStockMinimo())
                .unidadesParaReponer(q.getUnidadesParaReponer())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}