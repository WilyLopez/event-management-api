package com.playzone.pems.interfaces.rest.comercial;

import com.playzone.pems.application.comercial.dto.response.TipoEventoResponse;
import com.playzone.pems.application.comercial.port.in.GestionarTipoEventoUseCase;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tipos-evento")
@RequiredArgsConstructor
public class TipoEventoController {

    private final GestionarTipoEventoUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoEventoResponse>>> listarActivos() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarActivos()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<List<TipoEventoResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarTodos()));
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<TipoEventoResponse>> obtener(@PathVariable String codigo) {
        return ResponseEntity.ok(ApiResponse.ok(useCase.obtener(codigo)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<TipoEventoResponse>> crear(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(useCase.crear(body)));
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<TipoEventoResponse>> actualizar(
            @PathVariable String codigo,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok(useCase.actualizar(codigo, body)));
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String codigo) {
        useCase.eliminar(codigo);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
