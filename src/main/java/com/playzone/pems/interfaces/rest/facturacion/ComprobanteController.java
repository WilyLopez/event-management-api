package com.playzone.pems.interfaces.rest.facturacion;

import com.playzone.pems.application.facturacion.dto.command.AnularComprobanteCommand;
import com.playzone.pems.application.facturacion.dto.command.EmitirComprobanteCommand;
import com.playzone.pems.application.facturacion.dto.query.ComprobanteQuery;
import com.playzone.pems.application.facturacion.port.in.AnularComprobanteUseCase;
import com.playzone.pems.application.facturacion.port.in.EmitirComprobanteUseCase;
import com.playzone.pems.interfaces.rest.facturacion.request.EmitirComprobanteRequest;
import com.playzone.pems.interfaces.rest.facturacion.response.ComprobanteResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comprobantes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ComprobanteController {

    private final EmitirComprobanteUseCase emitirUseCase;
    private final AnularComprobanteUseCase anularUseCase;

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<ComprobanteResponse>> emitir(
            @PathVariable Long idSede,
            @Valid @RequestBody EmitirComprobanteRequest request) {

        ComprobanteQuery query = emitirUseCase.ejecutar(EmitirComprobanteCommand.builder()
                .idPago(request.getIdPago())
                .idSede(idSede)
                .tipoComprobante(request.getTipoComprobante())
                .tipoDocReceptor(request.getTipoDocReceptor())
                .nroDocReceptor(request.getNroDocReceptor())
                .razonSocialReceptor(request.getRazonSocialReceptor())
                .direccionReceptor(request.getDireccionReceptor())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PostMapping("/{idComprobante}/anular")
    public ResponseEntity<ApiResponse<ComprobanteResponse>> anular(
            @PathVariable Long idComprobante,
            @RequestParam String motivo,
            @RequestAttribute Long idUsuarioAdmin) {

        ComprobanteQuery query = anularUseCase.ejecutar(AnularComprobanteCommand.builder()
                .idComprobante(idComprobante)
                .motivoAnulacion(motivo)
                .idUsuario(idUsuarioAdmin)
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    private ComprobanteResponse toResponse(ComprobanteQuery q) {
        return ComprobanteResponse.builder()
                .id(q.getId())
                .numeroCompleto(q.getNumeroCompleto())
                .tipoComprobante(q.getTipoComprobante())
                .estadoComprobante(q.getEstadoComprobante())
                .razonSocialReceptor(q.getRazonSocialReceptor())
                .nroDocReceptor(q.getNroDocReceptor())
                .montoBase(q.getMontoBase())
                .montoIgv(q.getMontoIgv())
                .montoTotal(q.getMontoTotal())
                .pdfUrl(q.getPdfUrl())
                .cdrEstado(q.getCdrEstado())
                .fechaEmision(q.getFechaEmision())
                .build();
    }
}