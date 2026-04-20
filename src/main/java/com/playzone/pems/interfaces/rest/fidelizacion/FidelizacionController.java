package com.playzone.pems.interfaces.rest.fidelizacion;

import com.playzone.pems.application.fidelizacion.dto.query.HistorialFidelizacionQuery;
import com.playzone.pems.application.fidelizacion.port.in.RegistrarVisitaUseCase;
import com.playzone.pems.interfaces.rest.fidelizacion.response.HistorialFidelizacionResponse;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fidelizacion")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FidelizacionController {

    private final RegistrarVisitaUseCase registrarVisitaUseCase;

    @PostMapping("/reservas/{idReserva}/registrar-visita")
    public ResponseEntity<ApiResponse<HistorialFidelizacionResponse>> registrarVisita(
            @PathVariable Long idReserva) {

        HistorialFidelizacionQuery query = registrarVisitaUseCase.registrarVisita(idReserva);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    private HistorialFidelizacionResponse toResponse(HistorialFidelizacionQuery q) {
        return HistorialFidelizacionResponse.builder()
                .id(q.getId())
                .idCliente(q.getIdCliente())
                .idReservaPublica(q.getIdReservaPublica())
                .visitaNumero(q.getVisitaNumero())
                .esBeneficioAplicado(q.isEsBeneficioAplicado())
                .fechaRegistro(q.getFechaRegistro())
                .build();
    }
}