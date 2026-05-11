package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.port.in.GestionarSedeUseCase;
import com.playzone.pems.domain.usuario.model.Sede;
import com.playzone.pems.domain.usuario.repository.SedeRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SedeService implements GestionarSedeUseCase {

    private final SedeRepository sedeRepository;

    @Override
    public Sede obtener(Long idSede) {
        return sedeRepository.findById(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", idSede));
    }

    @Override
    @Transactional
    public Sede actualizar(Long idSede, ActualizarSedeCommand command) {
        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", idSede));

        return sedeRepository.save(sede.toBuilder()
                .nombre(command.nombre() != null ? command.nombre() : sede.getNombre())
                .direccion(command.direccion() != null ? command.direccion() : sede.getDireccion())
                .ciudad(command.ciudad() != null ? command.ciudad() : sede.getCiudad())
                .departamento(command.departamento() != null ? command.departamento() : sede.getDepartamento())
                .telefono(command.telefono())
                .correo(command.correo())
                .ruc(command.ruc())
                .build());
    }
}
