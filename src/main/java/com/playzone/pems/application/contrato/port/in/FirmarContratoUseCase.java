package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.query.ContratoQuery;

import java.util.UUID;

public interface FirmarContratoUseCase {

    ContratoQuery ejecutar(Long idContrato, UUID idUsuario);
}