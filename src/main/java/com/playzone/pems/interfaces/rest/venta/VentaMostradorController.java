package com.playzone.pems.interfaces.rest.venta;

import com.playzone.pems.application.venta.dto.query.VentaMostradorQuery;
import com.playzone.pems.application.venta.service.VentaMostradorService;
import com.playzone.pems.interfaces.rest.venta.request.RegistrarVentaMostradorRequest;
import com.playzone.pems.interfaces.rest.venta.response.VentaMostradorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ventas-mostrador")
@RequiredArgsConstructor
public class VentaMostradorController {

    private final VentaMostradorService ventaMostradorService;

    @PostMapping
    @PreAuthorize("hasAuthority('pos.vender')")
    @ResponseStatus(HttpStatus.CREATED)
    public VentaMostradorResponse registrar(
            @Valid @RequestBody RegistrarVentaMostradorRequest request) {
        VentaMostradorQuery query = ventaMostradorService.registrar(request.toCommand());
        return VentaMostradorResponse.from(query);
    }
}
