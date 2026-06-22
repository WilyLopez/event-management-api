package com.playzone.pems.application.fidelizacion.service;

import com.playzone.pems.application.fidelizacion.dto.query.HistorialFidelizacionQuery;
import com.playzone.pems.application.fidelizacion.port.in.OtorgarBeneficioUseCase;
import com.playzone.pems.application.fidelizacion.port.in.RegistrarVisitaUseCase;
import com.playzone.pems.domain.fidelizacion.model.FidelizacionConfig;
import com.playzone.pems.domain.fidelizacion.repository.FidelizacionConfigRepository;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.fidelizacion.exception.BeneficioNoAplicableException;
import com.playzone.pems.domain.fidelizacion.model.HistorialFidelizacion;
import com.playzone.pems.domain.fidelizacion.repository.HistorialFidelizacionRepository;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FidelizacionService implements RegistrarVisitaUseCase, OtorgarBeneficioUseCase {

    private final HistorialFidelizacionRepository historialRepository;
    private final ReservaPublicaRepository        reservaRepository;
    private final ClientePerfilRepository          clientePerfilRepository;
    private final FidelizacionConfigRepository     configRepository;

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

        int umbral = configRepository.findByIdSede(reserva.getIdSede())
                .map(FidelizacionConfig::getUmbral)
                .orElse(6);

        boolean esBeneficio = nuevaVisita % umbral == 0;

        HistorialFidelizacion registro = HistorialFidelizacion.builder()
                .idCliente(reserva.getIdCliente())
                .idReservaPublica(idReservaPublica)
                .visitaNumero(nuevaVisita)
                .esBeneficioAplicado(esBeneficio)
                .umbralAplicado(umbral)
                .build();

        clientePerfilRepository.incrementarContadorVisitas(reserva.getIdCliente());

        return toQuery(historialRepository.save(registro));
    }

    @Override
    @Transactional
    public HistorialFidelizacionQuery otorgarBeneficio(Long idCliente) {
        clientePerfilRepository.buscarPorId(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", idCliente));

        int visitasActuales = historialRepository.countVisitasByCliente(idCliente);
        
        // Al otorgar manualmente, buscamos el umbral configurado (ej. para la sede 1 por defecto si no hay contexto)
        int umbral = configRepository.findByIdSede(1L).map(FidelizacionConfig::getUmbral).orElse(6);

        if (visitasActuales < umbral) {
            throw new BeneficioNoAplicableException(idCliente, visitasActuales, umbral);
        }

        HistorialFidelizacion ultimo = HistorialFidelizacion.builder()
                .idCliente(idCliente)
                .visitaNumero(visitasActuales)
                .esBeneficioAplicado(true)
                .umbralAplicado(umbral)
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