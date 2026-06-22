package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.ExtraPaquete;
import com.playzone.pems.domain.comercial.repository.ExtraPaqueteRepository;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ExtraPaqueteEntity;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.ExtraPaqueteJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.PaqueteEventoJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExtraPaquetePersistenceAdapter implements ExtraPaqueteRepository {

    private final ExtraPaqueteJpaRepository extraJpa;
    private final PaqueteEventoJpaRepository paqueteJpa;

    @Override
    public List<ExtraPaquete> findActivosByPaquete(Long idPaquete) {
        return extraJpa.findByPaquete_IdAndActivoTrueOrderByOrdenAsc(idPaquete)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<ExtraPaquete> findByPaquete(Long idPaquete) {
        return extraJpa.findByPaquete_IdOrderByOrdenAsc(idPaquete)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<ExtraPaquete> findById(Long id) {
        return extraJpa.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional
    public ExtraPaquete save(ExtraPaquete extra) {
        var paquete = paqueteJpa.findById(extra.getIdPaquete())
                .orElseThrow(() -> new ResourceNotFoundException("Paquete", extra.getIdPaquete()));
        var entity = ExtraPaqueteEntity.builder()
                .id(extra.getId())
                .paquete(paquete)
                .nombre(extra.getNombre())
                .descripcion(extra.getDescripcion())
                .activo(extra.isActivo())
                .orden(extra.getOrden())
                .build();
        return toDomain(extraJpa.save(entity));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        extraJpa.desactivar(id);
    }

    private ExtraPaquete toDomain(ExtraPaqueteEntity e) {
        return ExtraPaquete.builder()
                .id(e.getId())
                .idPaquete(e.getPaquete().getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .activo(e.isActivo())
                .orden(e.getOrden())
                .build();
    }
}
