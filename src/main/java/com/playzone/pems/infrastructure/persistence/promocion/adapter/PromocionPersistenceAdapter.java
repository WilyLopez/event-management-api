package com.playzone.pems.infrastructure.persistence.promocion.adapter;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.repository.PromocionRepository;
import com.playzone.pems.infrastructure.persistence.promocion.jpa.PromocionJpaRepository;
import com.playzone.pems.infrastructure.persistence.promocion.mapper.PromocionEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PromocionPersistenceAdapter implements PromocionRepository {

    private final PromocionJpaRepository promocionJpa;
    private final PromocionEntityMapper  mapper;

    @Override public Optional<Promocion> findById(Long id) {
        return promocionJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Page<Promocion> findAll(Pageable pageable) {
        return promocionJpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override public List<Promocion> findAutomaticasVigentes(Long idSede, TipoDia tipoDia, LocalDate fecha) {
        return promocionJpa.findAutomaticasVigentes(idSede, tipoDia.getCodigo(), fecha)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Promocion save(Promocion promocion) {
        return mapper.toDomain(promocionJpa.save(mapper.toEntity(promocion)));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        promocionJpa.findById(id).ifPresent(p -> { p.setEsActivo(false); promocionJpa.save(p); });
    }
}
