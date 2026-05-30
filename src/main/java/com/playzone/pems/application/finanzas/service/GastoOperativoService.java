package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.ActualizarGastoOperativoCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarGastoOperativoCommand;
import com.playzone.pems.application.finanzas.dto.query.GastoOperativoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarGastoOperativoUseCase;
import com.playzone.pems.domain.finanzas.model.GastoOperativoDiario;
import com.playzone.pems.domain.finanzas.repository.GastoOperativoDiarioRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GastoOperativoService implements GestionarGastoOperativoUseCase {

    private final GastoOperativoDiarioRepository gastoOperativoRepository;

    @Override
    public GastoOperativoQuery registrar(RegistrarGastoOperativoCommand command) {
        GastoOperativoDiario gasto = GastoOperativoDiario.builder()
                .idSede(command.getIdSede())
                .fecha(command.getFecha())
                .descripcion(command.getDescripcion())
                .monto(command.getMonto())
                .comprobanteUrl(command.getComprobanteUrl())
                .idUsuarioRegistra(command.getIdUsuarioRegistra())
                .build();
        return toQuery(gastoOperativoRepository.save(gasto));
    }

    @Override
    public GastoOperativoQuery actualizar(ActualizarGastoOperativoCommand command) {
        GastoOperativoDiario existente = gastoOperativoRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gasto operativo no encontrado."));
        GastoOperativoDiario actualizado = GastoOperativoDiario.builder()
                .id(existente.getId())
                .idSede(existente.getIdSede())
                .fecha(command.getFecha())
                .descripcion(command.getDescripcion())
                .monto(command.getMonto())
                .comprobanteUrl(command.getComprobanteUrl())
                .idUsuarioRegistra(existente.getIdUsuarioRegistra())
                .build();
        return toQuery(gastoOperativoRepository.save(actualizado));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoOperativoQuery> listarPorFecha(Long idSede, LocalDate fecha) {
        return gastoOperativoRepository.findBySedeAndFecha(idSede, fecha).stream()
                .map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoOperativoQuery> listarPorRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return gastoOperativoRepository.findBySedeAndRangoFecha(idSede, inicio, fin).stream()
                .map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        gastoOperativoRepository.deleteById(id);
    }

    private GastoOperativoQuery toQuery(GastoOperativoDiario g) {
        return GastoOperativoQuery.builder()
                .id(g.getId())
                .idSede(g.getIdSede())
                .fecha(g.getFecha())
                .descripcion(g.getDescripcion())
                .monto(g.getMonto())
                .comprobanteUrl(g.getComprobanteUrl())
                .fechaCreacion(g.getFechaCreacion())
                .build();
    }
}
