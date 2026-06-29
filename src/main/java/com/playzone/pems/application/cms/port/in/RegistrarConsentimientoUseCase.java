package com.playzone.pems.application.cms.port.in;

import java.util.List;

public interface RegistrarConsentimientoUseCase {

    void registrar(String origen, Long referenciaId, List<String> tipos);
}
