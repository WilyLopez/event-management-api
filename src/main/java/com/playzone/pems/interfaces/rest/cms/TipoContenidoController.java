package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.TipoContenidoQuery;
import com.playzone.pems.domain.cms.repository.TipoContenidoRepository;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms/tipos-contenido")
@RequiredArgsConstructor
public class TipoContenidoController {

    private final TipoContenidoRepository tipoContenidoRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TipoContenidoResponse>>> listar() {
        List<TipoContenidoResponse> result = tipoContenidoRepository.findAll()
                .stream()
                .map(t -> TipoContenidoQuery.from(t))
                .map(TipoContenidoResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Getter
    @Builder
    public static class TipoContenidoResponse {
        private Long   id;
        private String codigo;
        private String descripcion;

        public static TipoContenidoResponse from(TipoContenidoQuery q) {
            return TipoContenidoResponse.builder()
                    .id(q.getId())
                    .codigo(q.getCodigo())
                    .descripcion(q.getDescripcion())
                    .build();
        }
    }
}
