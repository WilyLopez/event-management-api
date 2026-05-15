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

import java.time.LocalDateTime;

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
    @Transactional(readOnly = true)
    public Resena obtener(Long idResena) {
        return findOrThrow(idResena);
    }

    @Override
    @Transactional
    public Resena submit(SubmitCommand command) {
        if (command.calificacion() < 1 || command.calificacion() > 5) {
            throw new ValidationException("calificacion", "La calificación debe estar entre 1 y 5.");
        }
        Resena nueva = Resena.builder()
                .idCliente(command.idCliente())
                .idEventoPrivado(command.idEventoPrivado())
                .nombreAutor(command.nombreAutor())
                .contenido(command.contenido())
                .calificacion(command.calificacion())
                .fotoUrl(command.fotoUrl())
                .aprobada(false)
                .destacada(false)
                .mostrarHome(false)
                .build();
        return resenaRepository.save(nueva);
    }

    @Override
    @Transactional
    public Resena aprobar(Long idResena, Long idUsuarioAdmin) {
        Resena resena = findOrThrow(idResena);
        if (resena.isAprobada()) {
            throw new ValidationException("La reseña ya fue aprobada.");
        }
        return resenaRepository.save(resena.toBuilder()
                .aprobada(true)
                .mostrarHome(true)
                .idUsuarioAprueba(idUsuarioAdmin)
                .build());
    }

    @Override
    @Transactional
    public Resena responder(ResponderCommand command) {
        Resena resena = findOrThrow(command.idResena());
        return resenaRepository.save(resena.toBuilder()
                .respuestaAdmin(command.respuesta())
                .fechaRespuesta(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public void destacar(Long idResena) {
        Resena resena = findOrThrow(idResena);
        resenaRepository.save(resena.toBuilder().destacada(true).build());
    }

    @Override
    @Transactional
    public void quitarDestacado(Long idResena) {
        Resena resena = findOrThrow(idResena);
        resenaRepository.save(resena.toBuilder().destacada(false).build());
    }

    @Override
    @Transactional
    public void toggleMostrarHome(Long idResena, boolean mostrar) {
        Resena resena = findOrThrow(idResena);
        resenaRepository.save(resena.toBuilder().mostrarHome(mostrar).build());
    }

    @Override
    @Transactional
    public void rechazar(Long idResena) {
        findOrThrow(idResena);
        resenaRepository.deleteById(idResena);
    }

    private Resena findOrThrow(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resena", id));
    }
}
