package com.playzone.pems.application.comercial.port.in;

import com.playzone.pems.domain.comercial.model.ExtraPaquete;
import java.util.List;

public interface GestionarExtrasUseCase {
    List<ExtraPaquete> listarActivosPorPaquete(Long idPaquete);
    List<ExtraPaquete> listarPorPaquete(Long idPaquete);
    ExtraPaquete crear(ExtraPaquete extra);
    ExtraPaquete actualizar(ExtraPaquete extra);
    void eliminar(Long id);
}
