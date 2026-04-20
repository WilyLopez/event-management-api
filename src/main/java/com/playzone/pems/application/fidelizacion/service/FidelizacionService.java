package com.playzone.pems.application.fidelizacion.service;

import com.playzone.pems.application.fidelizacion.dto.query.HistorialFidelizacionQuery;
import com.playzone.pems.application.fidelizacion.port.in.OtorgarBeneficioUseCase;
import com.playzone.pems.application.fidelizacion.port.in.RegistrarVisitaUseCase;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.fidelizacion.exception.BeneficioNoAplicableException;
import com.playzone.pems.domain.fidelizacion.model.HistorialFidelizacion;
import com.playzone.pems.domain.fidelizacion.repository.HistorialFidelizacionRepository;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FidelizacionService implements RegistrarVisitaUseCase, OtorgarBeneficioUseCase {

    private final HistorialFidelizacionRepository historialRepository;
    private final ReservaPublicaRepository        reservaRepository;
    private final ClienteRepository               clienteRepository;

    @Value("${playzone.negocio.visitas-para-entrada-gratis:6}")
    private int visitasParaBeneficio;

    @Override
    @Transactional
    public HistorialFidelizacionQuery registrarVisita(Long idReservaPublica) {
        if (historialRepository.findByReservaPublica(idReservaPublica).isPresent()) {
            throw new ValidationException("La visita ya fue registrada para esa reserva.");
        }

        ReservaPublica reserva = reservaRepository.findById(idReservaPublica)
                .orElseThrow(() -> new ResourceNotFoundException("ReservaPublica", idReservaPublica));

        int visitasAnteriores = historialRepository.countVisitasByCliente(reserva.getIdCliente());
        int nuevaVisita       = visitasAnteriores + 1;
        boolean esBeneficio   = nuevaVisita % visitasParaBeneficio == 0;

        HistorialFidelizacion registro = HistorialFidelizacion.builder()
                .idCliente(reserva.getIdCliente())
                .idReservaPublica(idReservaPublica)
                .visitaNumero(nuevaVisita)
                .esBeneficioAplicado(esBeneficio)
                .build();

        clienteRepository.incrementarContadorVisitas(reserva.getIdCliente());

        return toQuery(historialRepository.save(registro));
    }

    @Override
    @Transactional
    public HistorialFidelizacionQuery otorgarBeneficio(Long idCliente) {
        clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", idCliente));

        int visitasActuales = historialRepository.countVisitasByCliente(idCliente);

        if (visitasActuales < visitasParaBeneficio) {
            throw new BeneficioNoAplicableException(idCliente, visitasActuales, visitasParaBeneficio);
        }

        HistorialFidelizacion ultimo = HistorialFidelizacion.builder()
                .idCliente(idCliente)
                .visitaNumero(visitasActuales)
                .esBeneficioAplicado(true)
                .build();

        return toQuery(historialRepository.save(ultimo));
    }

    private HistorialFidelizacionQuery toQuery(HistorialFidelizacion h) {
        return HistorialFidelizacionQuery.builder()
                .id(h.getId())
                .idCliente(h.getIdCliente())
                .idReservaPublica(h.getIdReservaPublica())
                .visitaNumero(h.getVisitaNumero())
                .esBeneficioAplicado(h.isEsBeneficioAplicado())
                .fechaRegistro(h.getFechaRegistro())
                .build();
    }
}