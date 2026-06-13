package com.playzone.pems.interfaces.rest.venta;

import com.playzone.pems.application.venta.dto.command.ProcesarVentaCommand;
import com.playzone.pems.application.venta.dto.query.VentaQuery;
import com.playzone.pems.application.venta.port.in.ConsultarVentasUseCase;
import com.playzone.pems.application.venta.port.in.ProcesarVentaUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.interfaces.rest.venta.request.ProcesarVentaRequest;
import com.playzone.pems.interfaces.rest.venta.response.VentaResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final ProcesarVentaUseCase   procesarUseCase;
    private final ConsultarVentasUseCase consultarUseCase;
    private final SupabaseAuthFacade     supabaseAuthFacade;

    @PostMapping("/sedes/{idSede}")
    @PreAuthorize("hasAuthority('pos.vender')")
    public ResponseEntity<ApiResponse<VentaResponse>> procesar(
            @PathVariable Long idSede,
            @Valid @RequestBody ProcesarVentaRequest request) {

        VentaQuery query = procesarUseCase.ejecutar(ProcesarVentaCommand.builder()
                .idSede(idSede)
                .createdBy(supabaseAuthFacade.usuarioActualId()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado")))
                .clienteId(request.getClienteId())
                .eventoId(request.getEventoId())
                .tipo(request.getTipo())
                .canalCodigo(request.getCanalCodigo())
                .fechaVisita(request.getFechaVisita())
                .nombreAcompanante(request.getNombreAcompanante())
                .dniAcompanante(request.getDniAcompanante())
                .telefonoAcompanante(request.getTelefonoAcompanante())
                .promocionId(request.getPromocionId())
                .efectivoRecibido(request.getEfectivoRecibido())
                .vuelto(request.getVuelto())
                .actaFirmada(request.isActaFirmada())
                .esAnticipada(request.isEsAnticipada())
                .notas(request.getNotas())
                .lineas(request.getLineas().stream()
                        .map(l -> ProcesarVentaCommand.LineaVentaCommand.builder()
                                .cantidad(l.getCantidad())
                                .precioUnitario(l.getPrecioUnitario())
                                .build())
                        .toList())
                .descuento(request.getDescuento())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @GetMapping("/{idVenta}")
    @PreAuthorize("hasAuthority('pos.vender')")
    public ResponseEntity<ApiResponse<VentaResponse>> consultar(@PathVariable Long idVenta) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(consultarUseCase.consultarPorId(idVenta))));
    }

    private VentaResponse toResponse(VentaQuery q) {
        return VentaResponse.builder()
                .id(q.getId())
                .idSede(q.getIdSede())
                .clienteId(q.getClienteId())
                .eventoId(q.getEventoId())
                .tipo(q.getTipo())
                .canalCodigo(q.getCanalCodigo())
                .fechaVisita(q.getFechaVisita())
                .subtotal(q.getSubtotal())
                .descuento(q.getDescuento())
                .total(q.getTotal())
                .notas(q.getNotas())
                .createdAt(q.getCreatedAt())
                .build();
    }
}
