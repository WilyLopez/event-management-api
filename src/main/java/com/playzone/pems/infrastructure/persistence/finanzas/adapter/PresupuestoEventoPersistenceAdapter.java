package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.PresupuestoEvento;
import com.playzone.pems.domain.finanzas.repository.PresupuestoEventoRepository;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.PresupuestoEventoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.PresupuestoEventoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PresupuestoEventoPersistenceAdapter implements PresupuestoEventoRepository {

    private final PresupuestoEventoJpaRepository jpaRepository;
    private final EventoPrivadoJpaRepository     eventoPrivadoJpaRepository;

    @Override
    public Optional<PresupuestoEvento> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PresupuestoEvento> findByEvento(Long idEventoPrivado) {
        return jpaRepository.findByEventoPrivado_IdOrderByFechaCreacionAsc(idEventoPrivado)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public BigDecimal sumMontoEstimadoByEvento(Long idEventoPrivado) {
        return jpaRepository.sumMontoEstimadoByEvento(idEventoPrivado);
    }

    @Override
    public BigDecimal sumMontoRealByEvento(Long idEventoPrivado) {
        return jpaRepository.sumMontoRealByEvento(idEventoPrivado);
    }

    @Override
    @Transactional
    public PresupuestoEvento save(PresupuestoEvento presupuesto) {
        EventoPrivadoEntity evento = eventoPrivadoJpaRepository
                .getReferenceById(presupuesto.getIdEventoPrivado());
        PresupuestoEventoEntity entity = PresupuestoEventoEntity.builder()
                .id(presupuesto.getId())
                .eventoPrivado(evento)
                .concepto(presupuesto.getConcepto())
                .categoria(presupuesto.getCategoria())
                .montoEstimado(presupuesto.getMontoEstimado())
                .montoReal(presupuesto.getMontoReal())
                .estado(presupuesto.getEstado())
                .idUsuarioRegistra(presupuesto.getIdUsuarioRegistra())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private PresupuestoEvento toDomain(PresupuestoEventoEntity e) {
        return PresupuestoEvento.builder()
                .id(e.getId())
                .idEventoPrivado(e.getEventoPrivado().getId())
                .concepto(e.getConcepto())
                .categoria(e.getCategoria())
                .montoEstimado(e.getMontoEstimado())
                .montoReal(e.getMontoReal())
                .estado(e.getEstado())
                .idUsuarioRegistra(e.getIdUsuarioRegistra())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }
}
