package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.query.ClienteQuery;

public interface HacerVipUseCase {
    ClienteQuery ejecutar(Long id, int descuentoPorcentaje);
}