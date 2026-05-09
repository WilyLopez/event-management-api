package com.playzone.pems.application.promocion.port.in;

import com.playzone.pems.application.promocion.dto.query.PromocionQuery;

import java.util.List;

public interface ListarPromocionesUseCase {
    List<PromocionQuery> listar();
}
