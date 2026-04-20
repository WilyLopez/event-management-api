package com.playzone.pems.application.pago.service;

import com.playzone.pems.application.pago.dto.command.RegistrarPagoCommand;
import com.playzone.pems.application.pago.dto.query.PagoQuery;
import com.playzone.pems.application.pago.port.in.RegistrarAdelantoEventoUseCase;
import com.playzone.pems.application.pago.port.in.RegistrarPagoReservaUseCase;
import com.playzone.pems.application.pago.port.in.RegistrarPagoVentaUseCase;
import com.playzone.pems.domain.pago.exception.PagoInvalidoException;
import com.playzone.pems.domain.pago.model.Pago;
import com.playzone.pems.domain.pago.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoService
        implements RegistrarPagoReservaUseCase,
        RegistrarAdelantoEventoUseCase,
        RegistrarPagoVentaUseCase {

    private final PagoRepository pagoRepository;

    @Override
    @Transactional
    public PagoQuery ejecutar(RegistrarPagoCommand command) {
        validarCommand(command);

        Pago pago = Pago.builder()
                .medioPago(command.getMedioPago())
                .tipoPago(command.getTipoPago())
                .idReservaPublica(command.getIdReservaPublica())
                .idEventoPrivado(command.getIdEventoPrivado())
                .idVenta(command.getIdVenta())
                .monto(command.getMonto())
                .referenciaPago(command.getReferenciaPago())
                .esParcial(command.getTipoPago().isEsParcial())
                .idUsuarioRegistra(command.getIdUsuarioRegistra())
                .build();

        if (!pago.contextoEsValido()) {
            throw new PagoInvalidoException("Debe especificar exactamente un contexto de pago.");
        }
        if (pago.requiereReferencia() && (command.getReferenciaPago() == null
                || command.getReferenciaPago().isBlank())) {
            throw new PagoInvalidoException("El medio de pago requiere un número de referencia/operación.");
        }

        return toQuery(pagoRepository.save(pago));
    }

    private void validarCommand(RegistrarPagoCommand command) {
        if (!command.getMedioPago().estaActivo()) {
            throw new PagoInvalidoException("El medio de pago '" + command.getMedioPago().getCodigo()
                    + "' no está habilitado.");
        }
    }

    private PagoQuery toQuery(Pago p) {
        return PagoQuery.builder()
                .id(p.getId())
                .medioPago(p.getMedioPago().getCodigo())
                .tipoPago(p.getTipoPago().getCodigo())
                .idReservaPublica(p.getIdReservaPublica())
                .idEventoPrivado(p.getIdEventoPrivado())
                .idVenta(p.getIdVenta())
                .monto(p.getMonto())
                .referenciaPago(p.getReferenciaPago())
                .esParcial(p.isEsParcial())
                .fechaPago(p.getFechaPago())
                .build();
    }
}