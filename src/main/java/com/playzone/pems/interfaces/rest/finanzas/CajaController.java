package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.AbrirCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.CerrarCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarMovimientoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.AperturaCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.MovimientoCajaQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarCajaUseCase;
import com.playzone.pems.interfaces.rest.finanzas.request.AbrirCajaRequest;
import com.playzone.pems.interfaces.rest.finanzas.request.CerrarCajaRequest;
import com.playzone.pems.interfaces.rest.finanzas.request.RegistrarMovimientoManualRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.AperturaCajaResponse;
import com.playzone.pems.interfaces.rest.finanzas.response.MovimientoCajaResponse;
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
@RequestMapping("/api/v1/caja")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CajaController {

    private final GestionarCajaUseCase useCase;

    @PostMapping("/sedes/{idSede}/abrir")
    public ResponseEntity<ApiResponse<AperturaCajaResponse>> abrir(
            @PathVariable Long idSede,
            @Valid @RequestBody AbrirCajaRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        AperturaCajaQuery query = useCase.abrir(AbrirCajaCommand.builder()
                .idSede(idSede)
                .fecha(request.getFecha())
                .saldoInicial(request.getSaldoInicial())
                .idUsuarioApertura(idUsuarioAdmin)
                .observaciones(request.getObservaciones())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{idApertura}/cerrar")
    public ResponseEntity<ApiResponse<AperturaCajaResponse>> cerrar(
            @PathVariable Long idApertura,
            @Valid @RequestBody CerrarCajaRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        AperturaCajaQuery query = useCase.cerrar(CerrarCajaCommand.builder()
                .idAperturaCaja(idApertura)
                .saldoFinal(request.getSaldoFinal())
                .idUsuarioCierre(idUsuarioAdmin)
                .observaciones(request.getObservaciones())
                .build());
        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @GetMapping("/sedes/{idSede}/fecha/{fecha}")
    public ResponseEntity<ApiResponse<AperturaCajaResponse>> obtenerPorFecha(
            @PathVariable Long idSede,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.obtenerPorSedeYFecha(idSede, fecha))));
    }

    @GetMapping("/sedes/{idSede}/rango")
    public ResponseEntity<ApiResponse<List<AperturaCajaResponse>>> listarPorRango(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<AperturaCajaResponse> body = useCase.listarPorRango(idSede, inicio, fin)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{idApertura}/movimientos")
    public ResponseEntity<ApiResponse<List<MovimientoCajaResponse>>> listarMovimientos(
            @PathVariable Long idApertura) {
        List<MovimientoCajaResponse> body = useCase.listarMovimientos(idApertura)
                .stream().map(this::toMovimientoResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping("/{idApertura}/movimientos")
    public ResponseEntity<ApiResponse<MovimientoCajaResponse>> registrarMovimiento(
            @PathVariable Long idApertura,
            @Valid @RequestBody RegistrarMovimientoManualRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        MovimientoCajaQuery query = useCase.registrarMovimiento(RegistrarMovimientoManualCommand.builder()
                .idAperturaCaja(idApertura)
                .tipo(request.getTipo())
                .concepto(request.getConcepto())
                .monto(request.getMonto())
                .medioPago(request.getMedioPago())
                .idUsuarioRegistra(idUsuarioAdmin)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toMovimientoResponse(query)));
    }

    @DeleteMapping("/movimientos/{idMovimiento}")
    public ResponseEntity<ApiResponse<Void>> eliminarMovimiento(@PathVariable Long idMovimiento) {
        useCase.eliminarMovimiento(idMovimiento);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private AperturaCajaResponse toResponse(AperturaCajaQuery q) {
        return AperturaCajaResponse.builder()
                .id(q.getId())
                .idSede(q.getIdSede())
                .fecha(q.getFecha())
                .saldoInicial(q.getSaldoInicial())
                .saldoFinal(q.getSaldoFinal())
                .totalIngresos(q.getTotalIngresos())
                .totalEgresos(q.getTotalEgresos())
                .estado(q.getEstado())
                .idUsuarioApertura(q.getIdUsuarioApertura())
                .idUsuarioCierre(q.getIdUsuarioCierre())
                .fechaApertura(q.getFechaApertura())
                .fechaCierre(q.getFechaCierre())
                .observaciones(q.getObservaciones())
                .build();
    }

    private MovimientoCajaResponse toMovimientoResponse(MovimientoCajaQuery q) {
        return MovimientoCajaResponse.builder()
                .id(q.getId())
                .idAperturaCaja(q.getIdAperturaCaja())
                .tipo(q.getTipo())
                .concepto(q.getConcepto())
                .monto(q.getMonto())
                .medioPago(q.getMedioPago())
                .idRegistroIngreso(q.getIdRegistroIngreso())
                .idRegistroEgreso(q.getIdRegistroEgreso())
                .idReservaPublica(q.getIdReservaPublica())
                .esManual(q.isEsManual())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}
