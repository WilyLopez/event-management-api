package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.CrearTipoIngresoCommand;
import com.playzone.pems.application.finanzas.dto.query.TipoIngresoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarTipoIngresoUseCase;
import com.playzone.pems.domain.finanzas.model.TipoIngreso;
import com.playzone.pems.domain.finanzas.repository.TipoIngresoRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
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
                .codigo(command.getCodigo())
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
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
    public void desactivar(String codigo) {
        TipoIngreso tipo = tipoIngresoRepository.findById(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("TipoIngreso", "codigo", codigo));
        if (tipo.isEsSistema()) {
            throw new IllegalStateException("No se puede modificar un registro del sistema.");
        }
        tipoIngresoRepository.desactivar(codigo);
    }

    private TipoIngresoQuery toQuery(TipoIngreso t) {
        return TipoIngresoQuery.builder()
                .codigo(t.getCodigo())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .esSistema(t.isEsSistema())
                .orden(t.getOrden())
                .activo(t.isActivo())
                .build();
    }
}
