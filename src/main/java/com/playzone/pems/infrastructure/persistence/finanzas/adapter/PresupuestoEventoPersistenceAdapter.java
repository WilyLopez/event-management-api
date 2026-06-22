package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.PresupuestoEvento;
import com.playzone.pems.domain.finanzas.repository.PresupuestoEventoRepository;
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

    @Override
    public Optional<PresupuestoEvento> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PresupuestoEvento> findByEvento(Long idEventoPrivado) {
        return jpaRepository.findByEventoIdOrderByCreatedAtAsc(idEventoPrivado)
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
        PresupuestoEventoEntity entity = PresupuestoEventoEntity.builder()
                .id(presupuesto.getId())
                .eventoId(presupuesto.getIdEventoPrivado())
                .concepto(presupuesto.getConcepto())
                .categoria(presupuesto.getCategoria())
                .montoEstimado(presupuesto.getMontoEstimado())
                .montoReal(presupuesto.getMontoReal())
                .estado(presupuesto.getEstado())
                .createdBy(presupuesto.getIdUsuarioRegistra())
                .updatedBy(presupuesto.getIdUsuarioEditor())
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
                .idEventoPrivado(e.getEventoId())
                .concepto(e.getConcepto())
                .categoria(e.getCategoria())
                .montoEstimado(e.getMontoEstimado())
                .montoReal(e.getMontoReal())
                .estado(e.getEstado())
                .idUsuarioRegistra(e.getCreatedBy())
                .idUsuarioEditor(e.getUpdatedBy())
                .fechaCreacion(e.getCreatedAt() != null ? e.getCreatedAt() : null)
                .fechaActualizacion(e.getUpdatedAt() != null ? e.getUpdatedAt() : null)
                .build();
    }
}
