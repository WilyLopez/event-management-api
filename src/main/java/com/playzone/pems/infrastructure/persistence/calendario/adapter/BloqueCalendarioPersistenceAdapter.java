package com.playzone.pems.infrastructure.persistence.calendario.adapter;

import com.playzone.pems.domain.calendario.model.BloqueCalendario;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.infrastructure.persistence.calendario.entity.BloqueCalendarioEntity;
import com.playzone.pems.infrastructure.persistence.calendario.jpa.BloqueCalendarioJpaRepository;
import com.playzone.pems.infrastructure.persistence.calendario.mapper.CalendarioEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BloqueCalendarioPersistenceAdapter implements BloqueCalendarioRepository {

    private final BloqueCalendarioJpaRepository bloqueJpa;
    private final SedeJpaRepository             sedeJpa;
    private final UsuarioAdminJpaRepository     adminJpa;
    private final CalendarioEntityMapper        mapper;

    @Override
    public Optional<BloqueCalendario> findById(Long id) {
        return bloqueJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<BloqueCalendario> findActivosBySede(Long idSede) {
        return bloqueJpa.findBySede_IdAndActivoTrue(idSede).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsBloqueActivoEnFecha(Long idSede, LocalDate fecha) {
        return bloqueJpa.existsBloqueActivoEnFecha(idSede, fecha);
    }

    @Override
    public boolean existsSolapamientoEnRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return bloqueJpa.existsSolapamientoEnRango(idSede, inicio, fin);
    }

    @Override
    @Transactional
    public BloqueCalendario save(BloqueCalendario bloque) {
        var sede = sedeJpa.findById(bloque.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", bloque.getIdSede()));
        var creador = adminJpa.findById(bloque.getIdUsuarioCreador())
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", bloque.getIdUsuarioCreador()));

        BloqueCalendarioEntity entity = BloqueCalendarioEntity.builder()
                .id(bloque.getId())
                .sede(sede)
                .fechaInicio(bloque.getFechaInicio())
                .fechaFin(bloque.getFechaFin())
                .motivo(bloque.getMotivo())
                .activo(bloque.isActivo())
                .usuarioCreador(creador)
                .build();

        return mapper.toDomain(bloqueJpa.save(entity));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        bloqueJpa.findById(id).ifPresent(b -> {
            b.setActivo(false);
            bloqueJpa.save(b);
        });
    }
}