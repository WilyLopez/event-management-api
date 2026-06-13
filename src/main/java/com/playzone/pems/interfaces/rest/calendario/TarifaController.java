package com.playzone.pems.interfaces.rest.calendario;

import com.playzone.pems.application.calendario.dto.command.ConfigurarTarifaCommand;
import com.playzone.pems.application.calendario.port.in.ConfigurarTarifaUseCase;
import com.playzone.pems.domain.calendario.model.Tarifa;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.domain.calendario.repository.TarifaRepository;
import com.playzone.pems.interfaces.rest.calendario.request.ConfigurarTarifaRequest;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.util.FechaUtil;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final ConfigurarTarifaUseCase configurarUseCase;
    private final TarifaRepository        tarifaRepository;
    private final FeriadoRepository       feriadoRepository;

    @GetMapping("/sedes/{idSede}")
    public ResponseEntity<ApiResponse<PrecioDiaResponse>> precioDia(
            @PathVariable Long idSede,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate dia = fecha != null ? fecha : FechaUtil.hoy();
        boolean esFeriado = feriadoRepository.existsByFecha(dia);
        TipoDia tipoDia = (FechaUtil.esFindeSemana(dia) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;

        Tarifa tarifa = tarifaRepository.findVigenteBySedeAndTipoDiaAndFecha(idSede, tipoDia, dia)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa", "sede", String.valueOf(idSede)));

        return ResponseEntity.ok(ApiResponse.ok(PrecioDiaResponse.builder()
                .precio(tarifa.getPrecio())
                .tipoDia(tipoDia.getCodigo())
                .esFindeSemanaOFeriado(tipoDia == TipoDia.FIN_SEMANA_FERIADO)
                .build()));
    }

    @PostMapping("/sedes/{idSede}")
    @PreAuthorize("hasAuthority('tarifa.gestionar')")
    public ResponseEntity<ApiResponse<Void>> configurar(
            @PathVariable Long idSede,
            @Valid @RequestBody ConfigurarTarifaRequest request) {

        configurarUseCase.ejecutar(ConfigurarTarifaCommand.builder()
                .idSede(idSede)
                .tipoDia(request.getTipoDia())
                .precio(request.getPrecio())
                .vigenciaDesde(request.getVigenciaDesde())
                .vigenciaHasta(request.getVigenciaHasta())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.noContent());
    }

    @Getter
    @Builder
    static class PrecioDiaResponse {
        private BigDecimal precio;
        private String     tipoDia;
        private boolean    esFindeSemanaOFeriado;
    }
}