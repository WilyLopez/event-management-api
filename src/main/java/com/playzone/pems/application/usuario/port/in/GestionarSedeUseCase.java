package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.domain.usuario.model.Sede;

public interface GestionarSedeUseCase {

    record ActualizarSedeCommand(
            String nombre,
            String direccion,
            String ciudad,
            String departamento,
            String telefono,
            String correo,
            String ruc
    ) {}

    Sede obtener(Long idSede);

    Sede actualizar(Long idSede, ActualizarSedeCommand command);
}
