package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RegistrarPagoCuotaRequest {

    @NotEmpty(message = "Se requiere al menos un medio de pago.")
    @Valid
    private List<PagoItemRequest> pagos;
}
