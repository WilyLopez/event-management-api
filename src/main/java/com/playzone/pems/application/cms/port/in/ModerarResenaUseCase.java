package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.domain.cms.model.Resena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ModerarResenaUseCase {

    Page<Resena> listar(boolean pendientes, Pageable pageable);

    Resena aprobar(Long idResena, Long idUsuarioAdmin);

    void rechazar(Long idResena);
}