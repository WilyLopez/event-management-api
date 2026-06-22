package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.domain.usuario.model.ClientePerfil;

public interface QuitarVipPerfilUseCase {

    ClientePerfil quitarVip(Long id);
}
