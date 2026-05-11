package com.playzone.pems.infrastructure.persistence.evento.adapter;

import com.playzone.pems.domain.evento.model.ChecklistEvento;
import com.playzone.pems.domain.evento.repository.ChecklistEventoRepository;
import com.playzone.pems.infrastructure.persistence.evento.entity.ChecklistEventoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ChecklistEventoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.mapper.ChecklistEventoEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChecklistEventoPersistenceAdapter implements ChecklistEventoRepository {

    private static final List<String> TAREAS_BASE = Arrays.asList(
        "Decoracion lista",
        "Sonido instalado",
        "Animador confirmado",
        "Catering preparado",
        "Personal asignado",
        "Area limpia y habilitada"
    );

    private final ChecklistEventoJpaRepository checklistJpa;
    private final EventoPrivadoJpaRepository   eventoJpa;
    private final UsuarioAdminJpaRepository    adminJpa;
    private final ChecklistEventoEntityMapper  mapper;

    @Override
    public Optional<ChecklistEvento> findById(Long id) {
        return checklistJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ChecklistEvento> findByEventoOrdenado(Long idEvento) {
        return checklistJpa.findByEventoPrivado_IdOrderByOrdenAsc(idEvento)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public ChecklistEvento save(ChecklistEvento checklist) {
        var evento  = eventoJpa.findById(checklist.getIdEventoPrivado())
                .orElseThrow(() -> new ResourceNotFoundException("EventoPrivado", checklist.getIdEventoPrivado()));
        var usuario = checklist.getIdUsuarioCompleto() != null
                ? adminJpa.findById(checklist.getIdUsuarioCompleto()).orElse(null) : null;
        return mapper.toDomain(checklistJpa.save(mapper.toEntity(checklist, evento, usuario)));
    }

    @Override
    @Transactional
    public void crearTareasBase(Long idEvento) {
        EventoPrivadoEntity evento = eventoJpa.findById(idEvento)
                .orElseThrow(() -> new ResourceNotFoundException("EventoPrivado", idEvento));
        for (int i = 0; i < TAREAS_BASE.size(); i++) {
            checklistJpa.save(ChecklistEventoEntity.builder()
                    .eventoPrivado(evento)
                    .tarea(TAREAS_BASE.get(i))
                    .orden(i + 1)
                    .build());
        }
    }
}