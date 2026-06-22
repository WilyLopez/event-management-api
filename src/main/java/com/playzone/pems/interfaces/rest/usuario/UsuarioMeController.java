package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.usuario.port.in.CambiarPasswordMeUseCase;
import com.playzone.pems.interfaces.rest.usuario.request.CambiarPasswordRequest;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios/me")
@RequiredArgsConstructor
public class UsuarioMeController {

    private final CambiarPasswordMeUseCase cambiarPasswordMeUseCase;

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            HttpServletRequest servletRequest) {

        String authHeader = servletRequest.getHeader("Authorization");
        String accessToken = authHeader.substring(7);

        cambiarPasswordMeUseCase.ejecutar(accessToken, request.getPasswordActual(), request.getNuevoPassword());
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
