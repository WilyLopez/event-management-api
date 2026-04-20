package com.playzone.pems.interfaces.rest.contrato;

import com.playzone.pems.application.contrato.dto.command.GenerarContratoCommand;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;
import com.playzone.pems.application.contrato.port.in.FirmarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.GenerarContratoUseCase;
import com.playzone.pems.interfaces.rest.contrato.request.GenerarContratoRequest;
import com.playzone.pems.interfaces.rest.contrato.response.ContratoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contratos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ContratoController {

    private final GenerarContratoUseCase generarUseCase;
    private final FirmarContratoUseCase  firmarUseCase;

    @PostMapping("/eventos/{idEvento}")
    public ResponseEntity<ApiResponse<ContratoResponse>> generar(
            @PathVariable Long idEvento,
            @Valid @RequestBody GenerarContratoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        ContratoQuery query = generarUseCase.ejecutar(GenerarContratoCommand.builder()
                .idEventoPrivado(idEvento)
                .idUsuarioRedactor(idUsuarioAdmin)
                .contenidoTexto(request.getContenidoTexto())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PostMapping("/{idContrato}/firmar")
    public ResponseEntity<ApiResponse<ContratoResponse>> firmar(@PathVariable Long idContrato) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(firmarUseCase.ejecutar(idContrato))));
    }

    private ContratoResponse toResponse(ContratoQuery q) {
        return ContratoResponse.builder()
                .id(q.getId())
                .idEventoPrivado(q.getIdEventoPrivado())
                .estado(q.getEstado())
                .archivoPdfUrl(q.getArchivoPdfUrl())
                .fechaFirma(q.getFechaFirma())
                .fechaCreacion(q.getFechaCreacion())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}