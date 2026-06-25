package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.command.ConfirmarEventoCommand;
import com.playzone.pems.application.evento.dto.command.VentaPagoItem;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ConfirmarEventoPrivadoUseCase {

    EventoPrivadoQuery ejecutar(ConfirmarEventoCommand command);

    /** Compatibilidad con el controlador hasta que se migre la capa REST (Fase 3). */
    default EventoPrivadoQuery ejecutar(Long idEvento, BigDecimal precioTotal,
                                        BigDecimal montoAdelanto,
                                        UUID idUsuarioGestor,
                                        String medioPago) {
        List<VentaPagoItem> pagos = (montoAdelanto != null && montoAdelanto.compareTo(BigDecimal.ZERO) > 0 && medioPago != null)
                ? List.of(VentaPagoItem.builder().medioPagoCodigo(medioPago).monto(montoAdelanto).build())
                : List.of();

        return ejecutar(ConfirmarEventoCommand.builder()
                .idEvento(idEvento)
                .precioTotal(precioTotal)
                .montoAdelanto(montoAdelanto)
                .idUsuarioGestor(idUsuarioGestor)
                .pagosAdelanto(pagos)
                .modalidadPago("AL_CONTADO")
                .build());
    }
}
