package com.playzone.pems.interfaces.rest.evento;

import com.playzone.pems.application.evento.dto.command.CalcularVentaCommand;
import com.playzone.pems.application.evento.dto.command.NinoVentaCommand;
import com.playzone.pems.application.evento.dto.command.PagoLineaCommand;
import com.playzone.pems.application.evento.dto.command.RegistrarVentaPresencialCommand;
import com.playzone.pems.application.evento.dto.query.ResumenVentaQuery;
import com.playzone.pems.application.evento.dto.query.VentaPresencialQuery;
import com.playzone.pems.application.evento.service.VentaPresencialService;
import com.playzone.pems.interfaces.rest.evento.request.RegistrarVentaPresencialRequest;
import com.playzone.pems.interfaces.rest.evento.response.ResumenVentaResponse;
import com.playzone.pems.interfaces.rest.evento.response.VentaPresencialResponse;
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
@RequestMapping("/api/v1/ventas-presenciales")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class VentaPresencialController {

    private final VentaPresencialService ventaService;

    @PostMapping("/calcular")
    public ResponseEntity<ApiResponse<ResumenVentaResponse>> calcular(
            @RequestParam Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVisita,
            @RequestParam(required = false) Long idPromocion,
            @RequestParam int cantidadNinos) {

        List<NinoVentaCommand> ninos = new java.util.ArrayList<>();
        for (int i = 0; i < cantidadNinos; i++) {
            ninos.add(NinoVentaCommand.builder().nombre("").edad(0).build());
        }

        ResumenVentaQuery q = ventaService.calcular(CalcularVentaCommand.builder()
                .idSede(idSede)
                .fechaVisita(fechaVisita)
                .ninos(ninos)
                .idPromocion(idPromocion)
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResumenResponse(q)));
    }

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<VentaPresencialResponse>> registrar(
            @PathVariable Long idSede,
            @Valid @RequestBody RegistrarVentaPresencialRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        List<NinoVentaCommand> ninos = request.getNinos().stream()
                .map(n -> NinoVentaCommand.builder()
                        .nombre(n.getNombre())
                        .edad(n.getEdad())
                        .build())
                .toList();

        List<PagoLineaCommand> pagos = request.getPagos().stream()
                .map(p -> PagoLineaCommand.builder()
                        .metodo(p.getMetodo())
                        .monto(p.getMonto())
                        .build())
                .toList();

        VentaPresencialQuery query = ventaService.registrar(
                RegistrarVentaPresencialCommand.builder()
                        .idSede(idSede)
                        .idCliente(request.getIdCliente())
                        .fechaVisita(request.getFechaVisita())
                        .nombreAcompanante(request.getNombreAcompanante())
                        .dniAcompanante(request.getDniAcompanante())
                        .ninos(ninos)
                        .idPromocion(request.getIdPromocion())
                        .pagos(pagos)
                        .efectivoRecibido(request.getEfectivoRecibido())
                        .actaFirmada(request.isActaFirmada())
                        .build(),
                idUsuarioAdmin);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    private ResumenVentaResponse toResumenResponse(ResumenVentaQuery q) {
        return ResumenVentaResponse.builder()
                .precioUnitario(q.getPrecioUnitario())
                .cantidadNinos(q.getCantidadNinos())
                .subtotal(q.getSubtotal())
                .descuento(q.getDescuento())
                .total(q.getTotal())
                .tipoDia(q.getTipoDia() != null ? q.getTipoDia().getCodigo() : null)
                .aforoDisponible(q.getAforoDisponible())
                .aforoMaximo(q.getAforoMaximo())
                .build();
    }

    private VentaPresencialResponse toResponse(VentaPresencialQuery q) {
        List<VentaPresencialResponse.TicketResumenResponse> tickets = q.getTickets().stream()
                .map(t -> VentaPresencialResponse.TicketResumenResponse.builder()
                        .idReserva(t.getIdReserva())
                        .numeroTicket(t.getNumeroTicket())
                        .nombreNino(t.getNombreNino())
                        .edadNino(t.getEdadNino())
                        .codigoQr(t.getCodigoQr())
                        .build())
                .toList();
        return VentaPresencialResponse.builder()
                .idVenta(q.getIdVenta())
                .idSede(q.getIdSede())
                .idCliente(q.getIdCliente())
                .fechaVisita(q.getFechaVisita())
                .nombreAcompanante(q.getNombreAcompanante())
                .dniAcompanante(q.getDniAcompanante())
                .subtotal(q.getSubtotal())
                .idPromocion(q.getIdPromocion())
                .descuento(q.getDescuento())
                .total(q.getTotal())
                .efectivoRecibido(q.getEfectivoRecibido())
                .vuelto(q.getVuelto())
                .esAnticipada(q.isEsAnticipada())
                .fechaCreacion(q.getFechaCreacion())
                .tickets(tickets)
                .build();
    }
}
