package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.GuardarPresupuestoCommand;
import com.playzone.pems.application.finanzas.dto.query.PresupuestoEventoQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarPresupuestoEventoUseCase;
import com.playzone.pems.domain.finanzas.model.PresupuestoEvento;
import com.playzone.pems.domain.finanzas.model.enums.EstadoPresupuesto;
import com.playzone.pems.domain.finanzas.repository.PresupuestoEventoRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PresupuestoService implements GestionarPresupuestoEventoUseCase {

    private final PresupuestoEventoRepository presupuestoEventoRepository;

    @Override
    public PresupuestoEventoQuery guardar(GuardarPresupuestoCommand command) {
        PresupuestoEvento presupuesto = PresupuestoEvento.builder()
                .idEventoPrivado(command.getIdEventoPrivado())
                .concepto(command.getConcepto())
                .categoria(command.getCategoria())
                .montoEstimado(command.getMontoEstimado())
                .estado(EstadoPresupuesto.PENDIENTE)
                .idUsuarioRegistra(command.getIdUsuarioRegistra())
                .build();
        return toQuery(presupuestoEventoRepository.save(presupuesto));
    }

    @Override
    public PresupuestoEventoQuery marcarEjecutado(Long id, BigDecimal montoReal) {
        PresupuestoEvento existente = presupuestoEventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida de presupuesto no encontrada."));
        PresupuestoEvento ejecutado = PresupuestoEvento.builder()
                .id(existente.getId())
                .idEventoPrivado(existente.getIdEventoPrivado())
                .concepto(existente.getConcepto())
                .categoria(existente.getCategoria())
                .montoEstimado(existente.getMontoEstimado())
                .montoReal(montoReal)
                .estado(EstadoPresupuesto.EJECUTADO)
                .idUsuarioRegistra(existente.getIdUsuarioRegistra())
                .fechaCreacion(existente.getFechaCreacion())
                .build();
        return toQuery(presupuestoEventoRepository.save(ejecutado));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoEventoQuery> listarPorEvento(Long idEventoPrivado) {
        return presupuestoEventoRepository.findByEvento(idEventoPrivado)
                .stream().map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        presupuestoEventoRepository.deleteById(id);
    }

    private PresupuestoEventoQuery toQuery(PresupuestoEvento p) {
        return PresupuestoEventoQuery.builder()
                .id(p.getId())
                .idEventoPrivado(p.getIdEventoPrivado())
                .concepto(p.getConcepto())
                .categoria(p.getCategoria())
                .montoEstimado(p.getMontoEstimado())
                .montoReal(p.getMontoReal())
                .estado(p.getEstado())
                .fechaCreacion(p.getFechaCreacion())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}
