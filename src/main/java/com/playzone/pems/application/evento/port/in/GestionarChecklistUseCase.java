package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.ChecklistEventoQuery;

import java.util.List;
import java.util.UUID;

public interface GestionarChecklistUseCase {

    List<ChecklistEventoQuery> listar(Long idEvento);

    ChecklistEventoQuery completar(Long idChecklist, UUID idUsuarioAdmin);

    ChecklistEventoQuery descompletar(Long idChecklist);
}
