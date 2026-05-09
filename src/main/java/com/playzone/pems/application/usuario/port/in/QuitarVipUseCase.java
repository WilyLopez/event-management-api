package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.query.ClienteQuery;

public interface QuitarVipUseCase {
    ClienteQuery quitarVip(Long id);
}