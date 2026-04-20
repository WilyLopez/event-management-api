package com.playzone.pems.interfaces.rest.venta;

import com.playzone.pems.application.venta.dto.command.ProcesarVentaCommand;
import com.playzone.pems.application.venta.dto.query.VentaQuery;
import com.playzone.pems.application.venta.port.in.ConsultarVentasUseCase;
import com.playzone.pems.application.venta.port.in.ProcesarVentaUseCase;
import com.playzone.pems.interfaces.rest.venta.request.ProcesarVentaRequest;
import com.playzone.pems.interfaces.rest.venta.response.VentaResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class VentaController {

    private final ProcesarVentaUseCase  procesarUseCase;
    private final ConsultarVentasUseCase consultarUseCase;

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<VentaResponse>> procesar(
            @PathVariable Long idSede,
            @Valid @RequestBody ProcesarVentaRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        VentaQuery query = procesarUseCase.ejecutar(ProcesarVentaCommand.builder()
                .idSede(idSede)
                .idUsuario(idUsuarioAdmin)
                .idReservaPublica(request.getIdReservaPublica())
                .idEventoPrivado(request.getIdEventoPrivado())
                .lineas(request.getLineas().stream()
                        .map(l -> ProcesarVentaCommand.LineaVentaCommand.builder()
                                .idProducto(l.getIdProducto())
                                .cantidad(l.getCantidad())
                                .build())
                        .toList())
                .descuento(request.getDescuento())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<ApiResponse<VentaResponse>> consultar(@PathVariable Long idVenta) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(consultarUseCase.consultarPorId(idVenta))));
    }

    private VentaResponse toResponse(VentaQuery q) {
        List<VentaResponse.DetalleVentaResponse> detalles = q.getDetalles() != null
                ? q.getDetalles().stream()
                .map(d -> VentaResponse.DetalleVentaResponse.builder()
                        .idProducto(d.getIdProducto())
                        .nombreProducto(d.getNombreProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotalLinea(d.getSubtotalLinea())
                        .build())
                .toList()
                : List.of();

        return VentaResponse.builder()
                .id(q.getId())
                .idSede(q.getIdSede())
                .subtotal(q.getSubtotal())
                .descuento(q.getDescuento())
                .total(q.getTotal())
                .fechaVenta(q.getFechaVenta())
                .detalles(detalles)
                .build();
    }
}