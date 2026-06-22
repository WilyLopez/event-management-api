package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.domain.comercial.model.BeneficioPaquete;
import java.util.List;

public interface GestionarBeneficiosUseCase {
    List<BeneficioPaquete> listarPorPaquete(Long idPaquete);
    BeneficioPaquete crear(BeneficioPaquete beneficio);
    BeneficioPaquete actualizar(BeneficioPaquete beneficio);
    void eliminar(Long id);
}
