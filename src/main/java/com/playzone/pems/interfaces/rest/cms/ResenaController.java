package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.port.in.ModerarResenaUseCase;
import com.playzone.pems.interfaces.rest.cms.response.ResenaResponse;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ModerarResenaUseCase moderarUseCase;

    @PostMapping("/{idResena}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ResenaResponse>> aprobar(
            @PathVariable Long idResena,
            @RequestAttribute Long idUsuarioAdmin) {

        var resena = moderarUseCase.aprobar(idResena, idUsuarioAdmin);
        return ResponseEntity.ok(ApiResponse.ok(ResenaResponse.builder()
                .id(resena.getId())
                .nombreAutor(resena.getNombreAutor())
                .contenido(resena.getContenido())
                .calificacion(resena.getCalificacion())
                .aprobada(resena.isAprobada())
                .fechaCreacion(resena.getFechaCreacion())
                .build()));
    }

    @DeleteMapping("/{idResena}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rechazar(@PathVariable Long idResena) {
        moderarUseCase.rechazar(idResena);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}