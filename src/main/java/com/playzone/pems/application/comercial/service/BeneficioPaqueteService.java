package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.comercial.port.in.GestionarBeneficiosUseCase;
import com.playzone.pems.domain.comercial.model.BeneficioPaquete;
import com.playzone.pems.domain.comercial.repository.BeneficioPaqueteRepository;
import com.playzone.pems.domain.comercial.repository.PaqueteEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficioPaqueteService implements GestionarBeneficiosUseCase {

    private final BeneficioPaqueteRepository repository;
    private final PaqueteEventoRepository paqueteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BeneficioPaquete> listarPorPaquete(Long idPaquete) {
        validarPaquete(idPaquete);
        return repository.findByPaquete(idPaquete);
    }

    @Override
    public BeneficioPaquete crear(BeneficioPaquete b) {
        validarPaquete(b.getIdPaquete());
        return repository.save(b);
    }

    @Override
    public BeneficioPaquete actualizar(BeneficioPaquete b) {
        repository.findById(b.getId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrado: " + b.getId()));
        return repository.save(b);
    }

    @Override
    public void eliminar(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrado: " + id));
        repository.deleteById(id);
    }

    private void validarPaquete(Long idPaquete) {
        paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + idPaquete));
    }
}
