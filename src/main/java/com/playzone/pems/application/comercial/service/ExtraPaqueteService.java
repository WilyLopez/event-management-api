package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.comercial.port.in.GestionarExtrasUseCase;
import com.playzone.pems.domain.comercial.model.ExtraPaquete;
import com.playzone.pems.domain.comercial.repository.ExtraPaqueteRepository;
import com.playzone.pems.domain.comercial.repository.PaqueteEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtraPaqueteService implements GestionarExtrasUseCase {

    private final ExtraPaqueteRepository repository;
    private final PaqueteEventoRepository paqueteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExtraPaquete> listarActivosPorPaquete(Long idPaquete) {
        return repository.findActivosByPaquete(idPaquete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExtraPaquete> listarPorPaquete(Long idPaquete) {
        return repository.findByPaquete(idPaquete);
    }

    @Override
    public ExtraPaquete crear(ExtraPaquete extra) {
        paqueteRepository.findById(extra.getIdPaquete())
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + extra.getIdPaquete()));
        return repository.save(extra);
    }

    @Override
    public ExtraPaquete actualizar(ExtraPaquete extra) {
        repository.findById(extra.getId())
                .orElseThrow(() -> new IllegalArgumentException("Extra no encontrado: " + extra.getId()));
        return repository.save(extra);
    }

    @Override
    public void eliminar(Long id) {
        repository.desactivar(id);
    }
}
