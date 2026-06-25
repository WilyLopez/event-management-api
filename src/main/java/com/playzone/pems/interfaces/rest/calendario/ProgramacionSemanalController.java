package com.playzone.pems.interfaces.rest.calendario;

import com.playzone.pems.application.calendario.dto.command.CrearProgramacionSemanalCommand;
import com.playzone.pems.application.calendario.dto.query.ProgramacionSemanalDto;
import com.playzone.pems.application.calendario.port.in.ProgramacionSemanalUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.interfaces.rest.calendario.request.CrearProgramacionSemanalRequest;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendario")
@RequiredArgsConstructor
public class ProgramacionSemanalController {

    private final ProgramacionSemanalUseCase programacionUseCase;
    private final SupabaseAuthFacade         supabaseAuthFacade;

    @PostMapping("/sedes/{idSede}/programaciones")
    @PreAuthorize("hasAuthority('calendario.programar')")
    public ResponseEntity<ApiResponse<ProgramacionSemanalDto>> crear(
            @PathVariable Long idSede,
            @Valid @RequestBody CrearProgramacionSemanalRequest request) {

        ProgramacionSemanalDto dto = programacionUseCase.crear(
                CrearProgramacionSemanalCommand.builder()
                        .idSede(idSede)
                        .idUsuarioAdmin(supabaseAuthFacade.usuarioActualId().orElseThrow())
                        .semanaInicio(request.getSemanaInicio())
                        .semanaFin(request.getSemanaFin())
                        .autoGenerada(false)
                        .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(dto));
    }

    /** URL incluye idSede para validar propiedad antes de cancelar. */
    @DeleteMapping("/sedes/{idSede}/programaciones/{id}")
    @PreAuthorize("hasAuthority('calendario.programar')")
    public ResponseEntity<ApiResponse<Void>> cancelar(
            @PathVariable Long idSede,
            @PathVariable Long id) {

        programacionUseCase.cancelar(idSede, id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @GetMapping("/sedes/{idSede}/programaciones")
    @PreAuthorize("hasAuthority('calendario.ver')")
    public ResponseEntity<ApiResponse<List<ProgramacionSemanalDto>>> listarFuturas(
            @PathVariable Long idSede) {

        return ResponseEntity.ok(ApiResponse.ok(programacionUseCase.listarFuturas(idSede)));
    }
}
