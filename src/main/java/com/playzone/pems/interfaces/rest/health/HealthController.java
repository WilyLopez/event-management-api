package com.playzone.pems.interfaces.rest.health;

import com.playzone.pems.domain.usuario.repository.PerfilUsuarioRepository;
import com.playzone.pems.domain.usuario.repository.StaffPerfilRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthContext;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final SupabaseAuthFacade      supabaseAuthFacade;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final StaffPerfilRepository   staffPerfilRepository;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
                "status",    "ok",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me() {
        SupabaseAuthContext ctx = supabaseAuthFacade.contextoActual()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado"));

        String nombre = perfilUsuarioRepository.buscarPorId(ctx.userId())
                .map(p -> p.getNombreCompleto())
                .orElse(null);

        String tipoPerfil;
        if (ctx.clientePerfilId() != null) {
            tipoPerfil = "CLIENTE";
        } else if (staffPerfilRepository.buscarPorUsuarioId(ctx.userId()).isPresent()) {
            tipoPerfil = "STAFF";
        } else {
            tipoPerfil = "NINGUNO";
        }

        return ResponseEntity.ok(ApiResponse.ok(new MeResponse(
                ctx.userId(),
                nombre,
                ctx.email(),
                ctx.roles(),
                ctx.permisos(),
                tipoPerfil
        )));
    }

    public record MeResponse(
            UUID         userId,
            String       nombre,
            String       correo,
            List<String> roles,
            List<String> permisos,
            String       tipoPerfil
    ) {}
}
