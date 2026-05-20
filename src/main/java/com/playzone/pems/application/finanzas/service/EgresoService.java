package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.RegistrarEgresoCommand;
import com.playzone.pems.application.finanzas.dto.query.RegistroEgresoQuery;
import com.playzone.pems.application.finanzas.port.in.RegistrarEgresoUseCase;
import com.playzone.pems.domain.finanzas.model.RegistroEgreso;
import com.playzone.pems.domain.finanzas.model.TipoEgreso;
import com.playzone.pems.domain.finanzas.repository.RegistroEgresoRepository;
import com.playzone.pems.domain.finanzas.repository.TipoEgresoRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EgresoService implements RegistrarEgresoUseCase {

    private final RegistroEgresoRepository registroEgresoRepository;
    private final TipoEgresoRepository     tipoEgresoRepository;

    @Override
    public RegistroEgresoQuery registrar(RegistrarEgresoCommand command) {
        TipoEgreso tipo = tipoEgresoRepository.findById(command.getIdTipoEgreso())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de egreso no encontrado."));
        if (!tipo.isActivo()) {
            throw new ValidationException("El tipo de egreso seleccionado no está activo.");
        }
        RegistroEgreso egreso = RegistroEgreso.builder()
                .idTipoEgreso(tipo.getId())
                .idSede(command.getIdSede())
                .monto(command.getMonto())
                .fecha(command.getFecha())
                .periodoAnio(command.getPeriodoAnio())
                .periodoMes(command.getPeriodoMes())
                .descripcion(command.getDescripcion())
                .comprobanteUrl(command.getComprobanteUrl())
                .esRecurrente(command.isEsRecurrente())
                .idUsuarioRegistra(command.getIdUsuarioRegistra())
                .build();
        return toQuery(registroEgresoRepository.save(egreso), tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroEgresoQuery> listar(Long idSede, Pageable pageable) {
        return registroEgresoRepository.findBySede(idSede, pageable)
                .map(r -> {
                    TipoEgreso tipo = tipoEgresoRepository.findById(r.getIdTipoEgreso()).orElse(null);
                    return toQuery(r, tipo);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroEgresoQuery> listarPorPeriodo(Long idSede, int anio, int mes) {
        return registroEgresoRepository.findBySedeAndPeriodo(idSede, anio, mes).stream()
                .map(r -> {
                    TipoEgreso tipo = tipoEgresoRepository.findById(r.getIdTipoEgreso()).orElse(null);
                    return toQuery(r, tipo);
                })
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        registroEgresoRepository.deleteById(id);
    }

    private RegistroEgresoQuery toQuery(RegistroEgreso r, TipoEgreso tipo) {
        return RegistroEgresoQuery.builder()
                .id(r.getId())
                .idTipoEgreso(r.getIdTipoEgreso())
                .nombreTipoEgreso(tipo != null ? tipo.getNombre() : null)
                .categoriaEgreso(tipo != null ? tipo.getCategoria() : null)
                .idSede(r.getIdSede())
                .monto(r.getMonto())
                .fecha(r.getFecha())
                .periodoAnio(r.getPeriodoAnio())
                .periodoMes(r.getPeriodoMes())
                .descripcion(r.getDescripcion())
                .comprobanteUrl(r.getComprobanteUrl())
                .esRecurrente(r.isEsRecurrente())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}
