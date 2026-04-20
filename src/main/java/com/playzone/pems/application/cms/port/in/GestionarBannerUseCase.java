package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.domain.cms.model.Banner;

import java.time.LocalDate;

public interface GestionarBannerUseCase {

    record CrearCommand(
            Long    idSede,
            String  titulo,
            String  descripcion,
            String  imagenUrl,
            String  enlaceDestino,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            int     orden,
            Long    idUsuario
    ) {}

    Banner crear(CrearCommand command);

    void desactivar(Long idBanner);

    void eliminar(Long idBanner);
}