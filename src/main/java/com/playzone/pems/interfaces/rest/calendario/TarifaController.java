package com.playzone.pems.interfaces.rest.calendario;

import com.playzone.pems.application.calendario.dto.command.ConfigurarTarifaCommand;
import com.playzone.pems.application.calendario.port.in.ConfigurarTarifaUseCase;
import com.playzone.pems.interfaces.rest.calendario.request.ConfigurarTarifaRequest;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tarifas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TarifaController {

    private final ConfigurarTarifaUseCase configurarUseCase;

    @PostMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<Void>> configurar(
            @PathVariable Long idSede,
            @Valid @RequestBody ConfigurarTarifaRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        configurarUseCase.ejecutar(ConfigurarTarifaCommand.builder()
                .idSede(idSede)
                .tipoDia(request.getTipoDia())
                .precio(request.getPrecio())
                .vigenciaDesde(request.getVigenciaDesde())
                .vigenciaHasta(request.getVigenciaHasta())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.noContent());
    }
}