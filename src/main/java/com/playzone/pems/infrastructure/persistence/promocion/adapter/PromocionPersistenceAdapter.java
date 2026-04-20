package com.playzone.pems.infrastructure.persistence.promocion.adapter;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.repository.PromocionRepository;
import com.playzone.pems.infrastructure.persistence.promocion.jpa.PromocionJpaRepository;
import com.playzone.pems.infrastructure.persistence.promocion.mapper.PromocionEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
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

    private final PromocionJpaRepository  promocionJpa;
    private final SedeJpaRepository       sedeJpa;
    private final UsuarioAdminJpaRepository adminJpa;
    private final PromocionEntityMapper   mapper;

    @Override public Optional<Promocion> findById(Long id) {
        return promocionJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Page<Promocion> findAll(Pageable pageable) {
        return promocionJpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override public List<Promocion> findAutomaticasVigentes(Long idSede, TipoDia tipoDia, LocalDate fecha) {
        return promocionJpa.findAutomaticasVigentes(idSede, tipoDia, fecha).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Promocion save(Promocion promocion) {
        var sede    = promocion.getIdSede() != null ? sedeJpa.findById(promocion.getIdSede()).orElse(null) : null;
        var creador = adminJpa.findById(promocion.getIdUsuarioCreador())
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", promocion.getIdUsuarioCreador()));
        return mapper.toDomain(promocionJpa.save(mapper.toEntity(promocion, sede, creador)));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        promocionJpa.findById(id).ifPresent(p -> { p.setActivo(false); promocionJpa.save(p); });
    }
}