package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.CrearTipoIngresoCommand;
import com.playzone.pems.application.finanzas.dto.query.TipoIngresoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarTipoIngresoUseCase;
import com.playzone.pems.domain.finanzas.model.TipoIngreso;
import com.playzone.pems.domain.finanzas.repository.TipoIngresoRepository;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoIngresoService implements GestionarTipoIngresoUseCase {

    private final TipoIngresoRepository tipoIngresoRepository;

    @Override
    public TipoIngresoQuery crear(CrearTipoIngresoCommand command) {
        boolean repetido = tipoIngresoRepository.findAll().stream()
                .filter(TipoIngreso::isActivo)
                .anyMatch(t -> t.getNombre().equalsIgnoreCase(command.getNombre()));
        if (repetido) {
            throw new ValidationException("Ya existe un tipo de ingreso activo con ese nombre.");
        }
        TipoIngreso tipo = TipoIngreso.builder()
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .categoria(command.getCategoria())
                .activo(true)
                .build();
        return toQuery(tipoIngresoRepository.save(tipo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoIngresoQuery> listar() {
        return tipoIngresoRepository.findAll().stream().map(this::toQuery).toList();
    }

    @Override
    public void desactivar(Long id) {
        tipoIngresoRepository.desactivar(id);
    }

    private TipoIngresoQuery toQuery(TipoIngreso t) {
        return TipoIngresoQuery.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .categoria(t.getCategoria())
                .activo(t.isActivo())
                .build();
    }
}
