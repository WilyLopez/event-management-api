package com.playzone.pems.application.promocion.port.in;

import com.playzone.pems.application.promocion.dto.command.CrearPromocionCommand;
import com.playzone.pems.application.promocion.dto.query.PromocionQuery;

public interface CrearPromocionUseCase {

    PromocionQuery ejecutar(CrearPromocionCommand command);
}