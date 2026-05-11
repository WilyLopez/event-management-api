package com.playzone.pems.application.evento.service;

import com.playzone.pems.application.evento.dto.query.ChecklistEventoQuery;
import com.playzone.pems.application.evento.port.in.GestionarChecklistUseCase;
import com.playzone.pems.domain.evento.model.ChecklistEvento;
import com.playzone.pems.domain.evento.repository.ChecklistEventoRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistEventoService implements GestionarChecklistUseCase {

    private final ChecklistEventoRepository checklistRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChecklistEventoQuery> listar(Long idEvento) {
        return checklistRepository.findByEventoOrdenado(idEvento)
                .stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional
    public ChecklistEventoQuery completar(Long idChecklist, Long idUsuarioAdmin) {
        ChecklistEvento item = checklistRepository.findById(idChecklist)
                .orElseThrow(() -> new ResourceNotFoundException("ChecklistEvento", idChecklist));

        if (item.isCompletada()) {
            throw new ValidationException("La tarea ya esta marcada como completada.");
        }

        return toQuery(checklistRepository.save(item.toBuilder()
                .completada(true)
                .idUsuarioCompleto(idUsuarioAdmin)
                .fechaCompletado(LocalDateTime.now(ZoneId.of("America/Lima")))
                .build()));
    }

    @Override
    @Transactional
    public ChecklistEventoQuery descompletar(Long idChecklist) {
        ChecklistEvento item = checklistRepository.findById(idChecklist)
                .orElseThrow(() -> new ResourceNotFoundException("ChecklistEvento", idChecklist));

        return toQuery(checklistRepository.save(item.toBuilder()
                .completada(false)
                .idUsuarioCompleto(null)
                .fechaCompletado(null)
                .build()));
    }

    private ChecklistEventoQuery toQuery(ChecklistEvento c) {
        return ChecklistEventoQuery.builder()
                .id(c.getId())
                .idEventoPrivado(c.getIdEventoPrivado())
                .tarea(c.getTarea())
                .completada(c.isCompletada())
                .orden(c.getOrden())
                .usuarioCompleto(c.getNombreUsuarioCompleto())
                .fechaCompletado(c.getFechaCompletado())
                .build();
    }
}