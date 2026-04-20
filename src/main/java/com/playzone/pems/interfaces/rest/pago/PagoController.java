package com.playzone.pems.interfaces.rest.pago;

import com.playzone.pems.application.pago.dto.command.RegistrarPagoCommand;
import com.playzone.pems.application.pago.dto.query.PagoQuery;
import com.playzone.pems.application.pago.port.in.RegistrarAdelantoEventoUseCase;
import com.playzone.pems.application.pago.port.in.RegistrarPagoReservaUseCase;
import com.playzone.pems.application.pago.port.in.RegistrarPagoVentaUseCase;
import com.playzone.pems.interfaces.rest.pago.request.RegistrarPagoRequest;
import com.playzone.pems.interfaces.rest.pago.response.PagoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PagoController {

    private final RegistrarPagoReservaUseCase   pagoReservaUseCase;
    private final RegistrarAdelantoEventoUseCase adelantoUseCase;
    private final RegistrarPagoVentaUseCase      pagoVentaUseCase;

    @PostMapping("/reserva")
    public ResponseEntity<ApiResponse<PagoResponse>> pagarReserva(
            @Valid @RequestBody RegistrarPagoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(pagoReservaUseCase.ejecutar(buildCommand(request, idUsuarioAdmin)))));
    }

    @PostMapping("/evento/adelanto")
    public ResponseEntity<ApiResponse<PagoResponse>> registrarAdelanto(
            @Valid @RequestBody RegistrarPagoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(adelantoUseCase.ejecutar(buildCommand(request, idUsuarioAdmin)))));
    }

    @PostMapping("/venta")
    public ResponseEntity<ApiResponse<PagoResponse>> pagarVenta(
            @Valid @RequestBody RegistrarPagoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(pagoVentaUseCase.ejecutar(buildCommand(request, idUsuarioAdmin)))));
    }

    private RegistrarPagoCommand buildCommand(RegistrarPagoRequest r, Long idUsuario) {
        return RegistrarPagoCommand.builder()
                .medioPago(r.getMedioPago())
                .tipoPago(r.getTipoPago())
                .idReservaPublica(r.getIdReservaPublica())
                .idEventoPrivado(r.getIdEventoPrivado())
                .idVenta(r.getIdVenta())
                .monto(r.getMonto())
                .referenciaPago(r.getReferenciaPago())
                .idUsuarioRegistra(idUsuario)
                .build();
    }

    private PagoResponse toResponse(PagoQuery q) {
        return PagoResponse.builder()
                .id(q.getId())
                .medioPago(q.getMedioPago())
                .tipoPago(q.getTipoPago())
                .monto(q.getMonto())
                .referenciaPago(q.getReferenciaPago())
                .esParcial(q.isEsParcial())
                .fechaPago(q.getFechaPago())
                .build();
    }
}