package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.domain.usuario.model.Sede;

import java.util.List;

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

    List<Sede> listar();

    Sede obtener(Long idSede);

    Sede actualizar(Long idSede, ActualizarSedeCommand command);
}
