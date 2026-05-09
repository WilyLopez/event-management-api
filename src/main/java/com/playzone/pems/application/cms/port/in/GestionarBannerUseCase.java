package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.BannerQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface GestionarBannerUseCase {

    record CrearCommand(
            Long      idSede,
            String    titulo,
            String    descripcion,
            String    imagenUrl,
            String    enlaceDestino,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            int       orden,
            Long      idUsuario
    ) {}

    record ActualizarCommand(
            Long      idBanner,
            Long      idSede,
            String    titulo,
            String    descripcion,
            String    imagenUrl,
            String    enlaceDestino,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            int       orden
    ) {}

    record ReordenarCommand(List<Long> idsOrdenados) {}

    BannerQuery    crear(CrearCommand command);
    BannerQuery    actualizar(ActualizarCommand command);
    Page<BannerQuery> listar(Pageable pageable);
    List<BannerQuery> listarPublicos(Long idSede);
    void           activar(Long idBanner);
    void           desactivar(Long idBanner);
    BannerQuery    duplicar(Long idBanner, Long idUsuario);
    void           reordenar(ReordenarCommand command);
    void           eliminar(Long idBanner);
}
