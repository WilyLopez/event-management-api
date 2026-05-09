package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.port.in.ModerarResenaUseCase;
import com.playzone.pems.domain.cms.model.Resena;
import com.playzone.pems.domain.cms.repository.ResenaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResenaService implements ModerarResenaUseCase {

    private final ResenaRepository resenaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Resena> listar(boolean pendientes, Pageable pageable) {
        return pendientes ? resenaRepository.findPendientes(pageable) 
                         : resenaRepository.findAprobadas(pageable);
    }

    @Override
    @Transactional
    public Resena aprobar(Long idResena, Long idUsuarioAdmin) {
        Resena resena = resenaRepository.findById(idResena)
                .orElseThrow(() -> new ResourceNotFoundException("Resena", idResena));

        if (resena.isAprobada()) {
            throw new ValidationException("La reseña ya fue aprobada.");
        }

        return resenaRepository.save(resena.toBuilder()
                .aprobada(true)
                .idUsuarioAprueba(idUsuarioAdmin)
                .build());
    }

    @Override
    @Transactional
    public void rechazar(Long idResena) {
        resenaRepository.findById(idResena)
                .orElseThrow(() -> new ResourceNotFoundException("Resena", idResena));
        resenaRepository.deleteById(idResena);
    }
}