package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.ChecklistEventoQuery;

import java.util.List;

public interface GestionarChecklistUseCase {

    List<ChecklistEventoQuery> listar(Long idEvento);

    ChecklistEventoQuery completar(Long idChecklist, Long idUsuarioAdmin);

    ChecklistEventoQuery descompletar(Long idChecklist);
}