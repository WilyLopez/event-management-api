package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.domain.cms.model.Resena;

public interface ModerarResenaUseCase {

    Resena aprobar(Long idResena, Long idUsuarioAdmin);

    void rechazar(Long idResena);
}