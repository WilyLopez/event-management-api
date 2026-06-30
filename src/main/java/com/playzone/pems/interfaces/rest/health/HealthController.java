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

        String nombre = null;
        String fotoPerfilUrl = null;
        var perfilOpt = perfilUsuarioRepository.buscarPorId(ctx.userId());
        if (perfilOpt.isPresent()) {
            nombre = perfilOpt.get().getNombreCompleto();
            fotoPerfilUrl = perfilOpt.get().getFotoPerfilPath();
        }

        String tipoPerfil;
        Long sedeId   = null;
        Long staffId  = null;
        if (ctx.clientePerfilId() != null) {
            tipoPerfil = "CLIENTE";
        } else {
            var staffOpt = staffPerfilRepository.buscarPorUsuarioId(ctx.userId());
            if (staffOpt.isPresent()) {
                tipoPerfil = "STAFF";
                sedeId  = staffOpt.get().getSedeId();
                staffId = staffOpt.get().getId();
            } else {
                tipoPerfil = "NINGUNO";
            }
        }

        boolean perfilCompleto = "STAFF".equals(tipoPerfil)
                || ("CLIENTE".equals(tipoPerfil) && ctx.clientePerfilId() != null);

        return ResponseEntity.ok(ApiResponse.ok(new MeResponse(
                ctx.userId(),
                nombre,
                ctx.email(),
                ctx.roles(),
                ctx.permisos(),
                tipoPerfil,
                ctx.clientePerfilId(),
                sedeId,
                staffId,
                perfilCompleto,
                fotoPerfilUrl
        )));
    }

    public record MeResponse(
            UUID         userId,
            String       nombre,
            String       correo,
            List<String> roles,
            List<String> permisos,
            String       tipoPerfil,
            Long         clientePerfilId,
            Long         sedeId,
            Long         staffId,
            boolean      perfilCompleto,
            String       fotoPerfilUrl
    ) {}
}
