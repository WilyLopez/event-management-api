package com.playzone.pems.interfaces.rest.usuario;

import com.playzone.pems.application.usuario.dto.command.ActualizarClienteCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClienteCommand;
import com.playzone.pems.application.usuario.dto.query.ClientePageQuery;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;
import com.playzone.pems.application.usuario.port.in.ActualizarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.ActivarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.DesactivarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.HacerVipUseCase;
import com.playzone.pems.application.usuario.port.in.ListarClientesUseCase;
import com.playzone.pems.application.usuario.port.in.ObtenerClienteUseCase;
import com.playzone.pems.application.usuario.port.in.QuitarVipUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarVisitaManualUseCase;
import com.playzone.pems.interfaces.rest.usuario.request.ActualizarClienteRequest;
import com.playzone.pems.interfaces.rest.usuario.request.HacerVipRequest;
import com.playzone.pems.interfaces.rest.usuario.request.RegistrarClienteRequest;
import com.playzone.pems.interfaces.rest.usuario.response.ClienteResponse;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final RegistrarClienteUseCase      registrarUseCase;
    private final ActualizarClienteUseCase     actualizarUseCase;
    private final ListarClientesUseCase        listarUseCase;
    private final ObtenerClienteUseCase        obtenerUseCase;
    private final ActivarClienteUseCase        activarUseCase;
    private final DesactivarClienteUseCase     desactivarUseCase;
    private final HacerVipUseCase              hacerVipUseCase;
    private final QuitarVipUseCase             quitarVipUseCase;
    private final RegistrarVisitaManualUseCase visitaManualUseCase;

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<ClienteResponse>> registrar(
            @Valid @RequestBody RegistrarClienteRequest request) {

        ClienteQuery query = registrarUseCase.ejecutar(
                RegistrarClienteCommand.builder()
                        .nombre(request.getNombre())
                        .correo(request.getCorreo())
                        .contrasena(request.getContrasena())
                        .telefono(request.getTelefono())
                        .dni(request.getDni())
                        .ruc(request.getRuc())
                        .razonSocial(request.getRazonSocial())
                        .direccionFiscal(request.getDireccionFiscal())
                        .build());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(toResponse(query)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<ClienteResponse>>> listar(
            @RequestParam(defaultValue = "0")                  int     page,
            @RequestParam(defaultValue = "15")                 int     size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String  sort,
            @RequestParam(required = false)                    String  search,
            @RequestParam(required = false)                    Boolean esVip,
            @RequestParam(required = false)                    Boolean activo,
            @RequestParam(required = false)                    Boolean verificado,
            @RequestParam(required = false)                    Boolean frecuente) {

        String[]       parts = sort.split(",");
        Sort.Direction dir   = parts.length > 1 && "asc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        ClientePageQuery resultado = listarUseCase.ejecutar(
                search, esVip, activo, verificado, frecuente,
                PageRequest.of(page, size, Sort.by(dir, parts[0])));

        PagedResponse<ClienteResponse> paginado = PagedResponse.<ClienteResponse>builder()
                .content(resultado.getContent().stream().map(this::toResponse).toList())
                .page(resultado.getPage())
                .size(resultado.getSize())
                .totalElements(resultado.getTotalElements())
                .totalPages(resultado.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(paginado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<ClienteResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(obtenerUseCase.ejecutar(id))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequest request) {

        ClienteQuery query = actualizarUseCase.ejecutar(id,
                ActualizarClienteCommand.builder()
                        .nombre(request.getNombre())
                        .telefono(request.getTelefono())
                        .ruc(request.getRuc())
                        .razonSocial(request.getRazonSocial())
                        .direccionFiscal(request.getDireccionFiscal())
                        .build());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(query)));
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        activarUseCase.activar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        desactivarUseCase.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{id}/vip")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClienteResponse>> hacerVip(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) HacerVipRequest request) {

        int descuento = (request != null) ? request.getDescuento() : 10;
        return ResponseEntity.ok(
                ApiResponse.ok(toResponse(hacerVipUseCase.ejecutar(id, descuento))));
    }

    @DeleteMapping("/{id}/vip")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClienteResponse>> quitarVip(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(toResponse(quitarVipUseCase.quitarVip(id))));
    }

    @PostMapping("/{id}/visitas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> registrarVisita(@PathVariable Long id) {
        visitaManualUseCase.ejecutarVisita(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
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
                .direccionFiscal(q.getDireccionFiscal())
                .fotoPerfil(q.getFotoPerfil())
                .ultimoLogin(q.getUltimoLogin())
                .fechaNacimiento(q.getFechaNacimiento())
                .tipoCliente(q.getTipoCliente())
                .esVip(q.isEsVip())
                .descuentoVip(q.getDescuentoVip())
                .contadorVisitas(q.getContadorVisitas())
                .correoVerificado(q.isCorreoVerificado())
                .activo(q.isActivo())
                .fechaCreacion(q.getFechaCreacion())
                .build();
    }
}