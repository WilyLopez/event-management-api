package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.usuario.port.in.AutenticarAdminUseCase;
import com.playzone.pems.application.usuario.port.in.AutenticarClienteUseCase;
import com.playzone.pems.interfaces.rest.usuario.request.LoginRequest;
import com.playzone.pems.interfaces.rest.usuario.response.TokenResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutenticarClienteUseCase autenticarCliente;
    private final AutenticarAdminUseCase   autenticarAdmin;

    @PostMapping("/cliente/login")
    public ResponseEntity<ApiResponse<TokenResponse>> loginCliente(
            @Valid @RequestBody LoginRequest request) {

        AutenticarClienteUseCase.Result result = autenticarCliente.ejecutar(
                new AutenticarClienteUseCase.Command(request.getCorreo(), request.getContrasena()));

        return ResponseEntity.ok(ApiResponse.ok(TokenResponse.builder()
                .token(result.token())
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

        return ResponseEntity.ok(ApiResponse.ok(TokenResponse.builder()
                .token(result.token())
                .idUsuario(result.idAdmin())
                .nombre(result.nombre())
                .idSede(result.idSede())
                .rol("ADMIN")
                .build()));
    }
}