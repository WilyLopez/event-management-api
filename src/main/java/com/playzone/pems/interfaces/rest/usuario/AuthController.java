package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.auth.service.RefreshTokenService;
import com.playzone.pems.application.usuario.port.in.AutenticarAdminUseCase;
import com.playzone.pems.application.usuario.port.in.AutenticarClienteUseCase;
import com.playzone.pems.application.usuario.port.out.GenerarTokenPort;
import com.playzone.pems.domain.auth.model.RefreshToken;
import com.playzone.pems.interfaces.rest.usuario.request.LoginRequest;
import com.playzone.pems.interfaces.rest.usuario.response.TokenResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutenticarClienteUseCase autenticarCliente;
    private final AutenticarAdminUseCase   autenticarAdmin;
    private final RefreshTokenService      refreshTokenService;
    private final GenerarTokenPort         generarTokenPort;

    @Value("${playzone.seguridad.jwt-expiracion-ms:900000}")
    private long accessExpirationMs;

    @PostMapping("/cliente/login")
    public ResponseEntity<ApiResponse<TokenResponse>> loginCliente(
            @Valid @RequestBody LoginRequest request) {

        AutenticarClienteUseCase.Result result = autenticarCliente.ejecutar(
                new AutenticarClienteUseCase.Command(request.getCorreo(), request.getContrasena()));

        RefreshToken refresh = refreshTokenService.crear(result.idCliente(), request.getCorreo(), "CLIENTE");

        return ResponseEntity.ok(ApiResponse.ok(TokenResponse.builder()
                .token(result.token())
                .refreshToken(refresh.getToken())
                .accessExpiresIn(accessExpirationMs)
                .idUsuario(result.idCliente())
                .nombre(result.nombre())
                .rol("CLIENTE")
                .build()));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<TokenResponse>> loginAdmin(
            @Valid @RequestBody LoginRequest request) {

        AutenticarAdminUseCase.Result result = autenticarAdmin.ejecutar(
                new AutenticarAdminUseCase.Command(request.getCorreo(), request.getContrasena()));

        RefreshToken refresh = refreshTokenService.crear(result.idAdmin(), request.getCorreo(), "ADMIN");

        return ResponseEntity.ok(ApiResponse.ok(TokenResponse.builder()
                .token(result.token())
                .refreshToken(refresh.getToken())
                .accessExpiresIn(accessExpirationMs)
                .idUsuario(result.idAdmin())
                .nombre(result.nombre())
                .idSede(result.idSede())
                .rol("ADMIN")
                .build()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) {

        RefreshToken renovado = refreshTokenService.renovar(request.getRefreshToken());

        String nuevoAccess = generarTokenPort.generarTokenAcceso(
                renovado.getIdUsuario(), renovado.getCorreo(), renovado.getTipoUsuario());

        return ResponseEntity.ok(ApiResponse.ok(RefreshResponse.builder()
                .accessToken(nuevoAccess)
                .refreshToken(renovado.getToken())
                .accessExpiresIn(accessExpirationMs)
                .build()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshRequest request) {
        if (request.getRefreshToken() != null) {
            refreshTokenService.revocar(request.getRefreshToken());
        }
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Getter
    @NoArgsConstructor
    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;
    }

    @Getter
    @lombok.Builder
    public static class RefreshResponse {
        private String accessToken;
        private String refreshToken;
        private long   accessExpiresIn;
    }
}
