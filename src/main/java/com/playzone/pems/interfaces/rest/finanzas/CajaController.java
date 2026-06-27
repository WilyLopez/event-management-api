package com.playzone.pems.interfaces.rest.finanzas;

import com.playzone.pems.application.finanzas.dto.command.AbrirCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.CerrarCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarArqueoCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarMovimientoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.AperturaCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.ArqueoCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.MovimientoCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.ResumenCajaQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarCajaUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.interfaces.rest.finanzas.request.AbrirCajaRequest;
import com.playzone.pems.interfaces.rest.finanzas.request.CerrarCajaRequest;
import com.playzone.pems.interfaces.rest.finanzas.request.RegistrarArqueoRequest;
import com.playzone.pems.interfaces.rest.finanzas.request.RegistrarMovimientoManualRequest;
import com.playzone.pems.interfaces.rest.finanzas.response.AperturaCajaResponse;
import com.playzone.pems.interfaces.rest.finanzas.response.ArqueoCajaResponse;
import com.playzone.pems.interfaces.rest.finanzas.response.MovimientoCajaResponse;
import com.playzone.pems.interfaces.rest.finanzas.response.ResumenCajaResponse;
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
public class CajaController {

    private final GestionarCajaUseCase useCase;
    private final SupabaseAuthFacade   supabaseAuthFacade;

    @PostMapping("/sedes/{idSede}/abrir")
    @PreAuthorize("hasAuthority('caja.abrir')")
    public ResponseEntity<ApiResponse<AperturaCajaResponse>> abrir(
            @PathVariable Long idSede,
            @Valid @RequestBody AbrirCajaRequest request) {
        AperturaCajaQuery query = useCase.abrir(AbrirCajaCommand.builder()
                .idSede(idSede)
                .fecha(request.getFecha())
                .saldoInicial(request.getSaldoInicial())
                .idUsuarioApertura(supabaseAuthFacade.usuarioActualId().orElseThrow())
                .observaciones(request.getObservaciones())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{idApertura}/cerrar")
    @PreAuthorize("hasAuthority('caja.cerrar')")
    public ResponseEntity<ApiResponse<AperturaCajaResponse>> cerrar(
            @PathVariable Long idApertura,
            @Valid @RequestBody CerrarCajaRequest request) {
        AperturaCajaQuery query = useCase.cerrar(CerrarCajaCommand.builder()
                .idAperturaCaja(idApertura)
                .saldoFinal(request.getSaldoFinal())
                .idUsuarioCierre(supabaseAuthFacade.usuarioActualId().orElseThrow())
                .observaciones(request.getObservaciones())
                .build());
        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @GetMapping("/sedes/{idSede}/hoy")
    @PreAuthorize("hasAuthority('caja.ver_historial') or hasAuthority('caja.abrir')")
    public ResponseEntity<ApiResponse<AperturaCajaResponse>> obtenerHoy(@PathVariable Long idSede) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.obtenerHoy(idSede))));
    }

    @GetMapping("/sedes/{idSede}/fecha/{fecha}")
    @PreAuthorize("hasAuthority('caja.ver_historial')")
    public ResponseEntity<ApiResponse<AperturaCajaResponse>> obtenerPorFecha(
            @PathVariable Long idSede,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.obtenerPorSedeYFecha(idSede, fecha))));
    }

    @GetMapping("/sedes/{idSede}/rango")
    @PreAuthorize("hasAuthority('caja.ver_historial')")
    public ResponseEntity<ApiResponse<List<AperturaCajaResponse>>> listarPorRango(
            @PathVariable Long idSede,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<AperturaCajaResponse> body = useCase.listarPorRango(idSede, inicio, fin)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{idApertura}/movimientos")
    @PreAuthorize("hasAuthority('caja.ver_historial')")
    public ResponseEntity<ApiResponse<List<MovimientoCajaResponse>>> listarMovimientos(
            @PathVariable Long idApertura) {
        List<MovimientoCajaResponse> body = useCase.listarMovimientos(idApertura)
                .stream().map(this::toMovimientoResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @PostMapping("/{idApertura}/movimientos")
    @PreAuthorize("hasAuthority('caja.movimiento')")
    public ResponseEntity<ApiResponse<MovimientoCajaResponse>> registrarMovimiento(
            @PathVariable Long idApertura,
            @Valid @RequestBody RegistrarMovimientoManualRequest request) {
        MovimientoCajaQuery query = useCase.registrarMovimiento(RegistrarMovimientoManualCommand.builder()
                .idAperturaCaja(idApertura)
                .tipo(request.getTipo())
                .concepto(request.getConcepto())
                .monto(request.getMonto())
                .medioPago(request.getMedioPago())
                .idUsuarioRegistra(supabaseAuthFacade.usuarioActualId().orElseThrow())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toMovimientoResponse(query)));
    }

    @DeleteMapping("/movimientos/{idMovimiento}")
    @PreAuthorize("hasAuthority('caja.movimiento')")
    public ResponseEntity<ApiResponse<Void>> eliminarMovimiento(@PathVariable Long idMovimiento) {
        useCase.eliminarMovimiento(idMovimiento);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/{idApertura}/arqueos")
    @PreAuthorize("hasAuthority('caja.cerrar') or hasAuthority('caja.movimiento')")
    public ResponseEntity<ApiResponse<ArqueoCajaResponse>> registrarArqueo(
            @PathVariable Long idApertura,
            @Valid @RequestBody RegistrarArqueoRequest request) {
        ArqueoCajaQuery query = useCase.registrarArqueo(RegistrarArqueoCommand.builder()
                .idAperturaCaja(idApertura)
                .saldoContado(request.getSaldoContado())
                .observaciones(request.getObservaciones())
                .realizadoPor(supabaseAuthFacade.usuarioActualId().orElseThrow())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toArqueoResponse(query)));
    }

    @GetMapping("/{idApertura}/arqueos")
    @PreAuthorize("hasAuthority('caja.ver_historial')")
    public ResponseEntity<ApiResponse<List<ArqueoCajaResponse>>> listarArqueos(
            @PathVariable Long idApertura) {
        List<ArqueoCajaResponse> body = useCase.listarArqueos(idApertura)
                .stream().map(this::toArqueoResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{idApertura}/resumen")
    @PreAuthorize("hasAuthority('caja.ver_historial') or hasAuthority('caja.cerrar')")
    public ResponseEntity<ApiResponse<ResumenCajaResponse>> resumen(@PathVariable Long idApertura) {
        ResumenCajaQuery query = useCase.generarResumen(idApertura);
        return ResponseEntity.ok(ApiResponse.ok(toResumenResponse(query)));
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
                .saldoEsperado(q.getSaldoEsperado())
                .diferencia(q.getDiferencia())
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
                .idVenta(q.getIdVenta())
                .esManual(q.isEsManual())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }

    private ArqueoCajaResponse toArqueoResponse(ArqueoCajaQuery q) {
        return ArqueoCajaResponse.builder()
                .id(q.getId())
                .idAperturaCaja(q.getIdAperturaCaja())
                .saldoEsperado(q.getSaldoEsperado())
                .saldoContado(q.getSaldoContado())
                .diferencia(q.getDiferencia())
                .observaciones(q.getObservaciones())
                .realizadoPor(q.getRealizadoPor())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }

    private ResumenCajaResponse toResumenResponse(ResumenCajaQuery q) {
        return ResumenCajaResponse.builder()
                .id(q.getId())
                .idSede(q.getIdSede())
                .fecha(q.getFecha())
                .saldoInicial(q.getSaldoInicial())
                .totalIngresos(q.getTotalIngresos())
                .totalEgresos(q.getTotalEgresos())
                .saldoEsperado(q.getSaldoEsperado())
                .saldoFinal(q.getSaldoFinal())
                .diferencia(q.getDiferencia())
                .estado(q.getEstado())
                .fechaApertura(q.getFechaApertura())
                .fechaCierre(q.getFechaCierre())
                .observaciones(q.getObservaciones())
                .movimientos(q.getMovimientos() != null
                        ? q.getMovimientos().stream().map(this::toMovimientoResponse).toList() : null)
                .arqueos(q.getArqueos() != null
                        ? q.getArqueos().stream().map(this::toArqueoResponse).toList() : null)
                .build();
    }
}
