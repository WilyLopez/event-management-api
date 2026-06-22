package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.domain.usuario.model.ClientePerfil;

import java.math.BigDecimal;

public interface HacerVipPerfilUseCase {

    ClientePerfil ejecutar(Long id, BigDecimal descuento);
}
