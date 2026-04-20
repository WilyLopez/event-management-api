package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.usuario.dto.command.ActualizarClienteCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClienteCommand;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;
import com.playzone.pems.application.usuario.port.in.ActualizarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClienteUseCase;
import com.playzone.pems.interfaces.rest.usuario.request.ActualizarClienteRequest;
import com.playzone.pems.interfaces.rest.usuario.request.RegistrarClienteRequest;
import com.playzone.pems.interfaces.rest.usuario.response.ClienteResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final RegistrarClienteUseCase  registrarUseCase;
    private final ActualizarClienteUseCase actualizarUseCase;

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<ClienteResponse>> registrar(
            @Valid @RequestBody RegistrarClienteRequest request) {

        ClienteQuery query = registrarUseCase.ejecutar(RegistrarClienteCommand.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .contrasena(request.getContrasena())
                .telefono(request.getTelefono())
                .dni(request.getDni())
                .ruc(request.getRuc())
                .razonSocial(request.getRazonSocial())
                .direccionFiscal(request.getDireccionFiscal())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequest request) {

        ClienteQuery query = actualizarUseCase.ejecutar(id, ActualizarClienteCommand.builder()
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .ruc(request.getRuc())
                .razonSocial(request.getRazonSocial())
                .direccionFiscal(request.getDireccionFiscal())
                .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    private ClienteResponse toResponse(ClienteQuery q) {
        return ClienteResponse.builder()
                .id(q.getId())
                .nombre(q.getNombre())
                .correo(q.getCorreo())
                .telefono(q.getTelefono())
                .dni(q.getDni())
                .ruc(q.getRuc())
                .razonSocial(q.getRazonSocial())
                .esVip(q.isEsVip())
                .contadorVisitas(q.getContadorVisitas())
                .correoVerificado(q.isCorreoVerificado())
                .activo(q.isActivo())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}