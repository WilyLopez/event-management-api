package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.ActualizarEgresoCommand;
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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EgresoService implements RegistrarEgresoUseCase {

    private final RegistroEgresoRepository registroEgresoRepository;
    private final TipoEgresoRepository     tipoEgresoRepository;

    @Override
    public RegistroEgresoQuery registrar(RegistrarEgresoCommand command) {
        TipoEgreso tipo = tipoEgresoRepository.findById(command.getTipoEgresoCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de egreso no encontrado."));
        if (!tipo.isActivo()) {
            throw new ValidationException("El tipo de egreso seleccionado no está activo.");
        }
        RegistroEgreso egreso = RegistroEgreso.builder()
                .tipoEgresoCodigo(command.getTipoEgresoCodigo())
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
        return toQuery(registroEgresoRepository.save(egreso));
    }

    @Override
    public RegistroEgresoQuery actualizar(ActualizarEgresoCommand command) {
        RegistroEgreso existente = registroEgresoRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Egreso no encontrado."));
        TipoEgreso tipo = tipoEgresoRepository.findById(command.getTipoEgresoCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de egreso no encontrado."));
        if (!tipo.isActivo()) {
            throw new ValidationException("El tipo de egreso seleccionado no está activo.");
        }
        RegistroEgreso actualizado = RegistroEgreso.builder()
                .id(existente.getId())
                .tipoEgresoCodigo(command.getTipoEgresoCodigo())
                .idSede(existente.getIdSede())
                .monto(command.getMonto())
                .fecha(command.getFecha())
                .periodoAnio(command.getPeriodoAnio())
                .periodoMes(command.getPeriodoMes())
                .descripcion(command.getDescripcion())
                .comprobanteUrl(command.getComprobanteUrl())
                .esRecurrente(command.isEsRecurrente())
                .idUsuarioRegistra(existente.getIdUsuarioRegistra())
                .build();
        return toQuery(registroEgresoRepository.save(actualizado));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroEgresoQuery> listar(Long idSede, Pageable pageable) {
        return registroEgresoRepository.findBySede(idSede, pageable).map(this::toQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroEgresoQuery> listarPorPeriodo(Long idSede, int anio, int mes) {
        return registroEgresoRepository.findBySedeAndPeriodo(idSede, anio, mes).stream()
                .map(this::toQuery)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroEgresoQuery> listarPorRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return registroEgresoRepository.findBySedeAndRangoFecha(idSede, inicio, fin).stream()
                .map(this::toQuery)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        registroEgresoRepository.deleteById(id);
    }

    private RegistroEgresoQuery toQuery(RegistroEgreso r) {
        return RegistroEgresoQuery.builder()
                .id(r.getId())
                .tipoEgresoCodigo(r.getTipoEgresoCodigo())
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
