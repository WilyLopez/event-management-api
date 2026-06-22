package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.port.in.ModerarResenaUseCase;
import com.playzone.pems.domain.cms.model.Resena;
import com.playzone.pems.domain.cms.repository.ResenaRepository;
import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResenaService implements ModerarResenaUseCase {

    private final ResenaRepository         resenaRepository;
    private final EventoPrivadoRepository eventoPrivadoRepository;

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
        if (command.idCliente() == null) {
            throw new ValidationException("idCliente", "Debes estar autenticado para enviar una opinión.");
        }
        if (command.idEventoPrivado() == null) {
            throw new ValidationException("idEventoPrivado", "El ID del evento privado es obligatorio.");
        }

        // Evitar duplicidad por evento
        if (resenaRepository.existsByIdEventoPrivado(command.idEventoPrivado())) {
            throw new ValidationException("idEventoPrivado", "Ya has enviado una opinión para este evento.");
        }

        // Obtener el evento
        EventoPrivado evento = eventoPrivadoRepository.findById(command.idEventoPrivado())
                .orElseThrow(() -> new ResourceNotFoundException("EventoPrivado", command.idEventoPrivado()));

        // Verificar que el evento pertenezca al cliente
        if (!evento.getIdCliente().equals(command.idCliente())) {
            throw new ValidationException("idEventoPrivado", "No puedes calificar un evento que no te pertenece.");
        }

        // Verificar que el evento esté COMPLETADO
        if (evento.getEstado() != EstadoEventoPrivado.COMPLETADA) {
            throw new ValidationException("idEventoPrivado", "Solo puedes calificar eventos que hayan finalizado exitosamente.");
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
    public Resena aprobar(Long idResena, UUID idUsuarioAdmin) {
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
                .fechaRespuesta(OffsetDateTime.now())
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
