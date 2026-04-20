package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.command.EditarContenidoCommand;
import com.playzone.pems.application.cms.dto.query.ContenidoWebQuery;
import com.playzone.pems.application.cms.port.in.EditarContenidoWebUseCase;
import com.playzone.pems.interfaces.rest.cms.request.EditarContenidoRequest;
import com.playzone.pems.interfaces.rest.cms.response.ContenidoWebResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contenido")
@RequiredArgsConstructor
public class ContenidoWebController {

    private final EditarContenidoWebUseCase editarUseCase;

    @PutMapping("/{idContenido}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ContenidoWebResponse>> editar(
            @PathVariable Long idContenido,
            @Valid @RequestBody EditarContenidoRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        ContenidoWebQuery query = editarUseCase.ejecutar(EditarContenidoCommand.builder()
                .idContenido(idContenido)
                .valorEs(request.getValorEs())
                .valorEn(request.getValorEn())
                .idUsuarioEditor(idUsuarioAdmin)
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    private ContenidoWebResponse toResponse(ContenidoWebQuery q) {
        return ContenidoWebResponse.builder()
                .id(q.getId())
                .idSeccion(q.getIdSeccion())
                .clave(q.getClave())
                .valorEs(q.getValorEs())
                .valorEn(q.getValorEn())
                .activo(q.isActivo())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}