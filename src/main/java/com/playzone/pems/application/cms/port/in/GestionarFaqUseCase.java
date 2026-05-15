package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.FaqQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GestionarFaqUseCase {

    record CrearCommand(String pregunta, String respuesta, int ordenVisualizacion, Long idUsuario) {}

    record ActualizarCommand(Long idFaq, String pregunta, String respuesta, int ordenVisualizacion, Long idUsuario) {}

    record ReordenarCommand(List<Long> idsOrdenados) {}

    FaqQuery crear(CrearCommand command);

    FaqQuery actualizar(ActualizarCommand command);

    Page<FaqQuery> listar(Pageable pageable);

    List<FaqQuery> listarVisibles();

    void activar(Long idFaq);

    void desactivar(Long idFaq);

    void reordenar(ReordenarCommand command);

    void eliminar(Long idFaq);
}
