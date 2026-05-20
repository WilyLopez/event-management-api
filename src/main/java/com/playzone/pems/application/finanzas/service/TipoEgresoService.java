package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.CrearTipoEgresoCommand;
import com.playzone.pems.application.finanzas.dto.query.TipoEgresoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarTipoEgresoUseCase;
import com.playzone.pems.domain.finanzas.model.TipoEgreso;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import com.playzone.pems.domain.finanzas.repository.TipoEgresoRepository;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoEgresoService implements GestionarTipoEgresoUseCase {

    private final TipoEgresoRepository tipoEgresoRepository;

    @Override
    public TipoEgresoQuery crear(CrearTipoEgresoCommand command) {
        boolean nombreRepetido = tipoEgresoRepository.findAllActivos().stream()
                .anyMatch(t -> t.getNombre().equalsIgnoreCase(command.getNombre()));
        if (nombreRepetido) {
            throw new ValidationException("Ya existe un tipo de egreso activo con ese nombre.");
        }
        CategoriaEgreso categoria;
        try {
            categoria = CategoriaEgreso.valueOf(command.getCategoria());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Categoría inválida: " + command.getCategoria());
        }
        TipoEgreso tipo = TipoEgreso.builder()
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .categoria(categoria)
                .activo(true)
                .idUsuarioCreador(command.getIdUsuarioCreador())
                .build();
        return toQuery(tipoEgresoRepository.save(tipo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoEgresoQuery> listar() {
        return tipoEgresoRepository.findAllActivos().stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoEgresoQuery> listarPorCategoria(String categoria) {
        CategoriaEgreso cat;
        try {
            cat = CategoriaEgreso.valueOf(categoria);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Categoría inválida: " + categoria);
        }
        return tipoEgresoRepository.findByCategoria(cat).stream().map(this::toQuery).toList();
    }

    @Override
    public void desactivar(Long id) {
        tipoEgresoRepository.desactivar(id);
    }

    private TipoEgresoQuery toQuery(TipoEgreso t) {
        return TipoEgresoQuery.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .categoria(t.getCategoria())
                .activo(t.isActivo())
                .build();
    }
}
