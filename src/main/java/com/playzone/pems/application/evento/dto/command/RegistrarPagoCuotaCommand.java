package com.playzone.pems.application.evento.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RegistrarPagoCuotaCommand {
    private final Long               idCuota;
    private final List<VentaPagoItem> pagos;
    private final UUID               idUsuario;
}
