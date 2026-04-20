package com.playzone.pems.infrastructure.persistence.calendario.adapter;

import com.playzone.pems.domain.calendario.model.Feriado;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.infrastructure.persistence.calendario.entity.FeriadoEntity;
import com.playzone.pems.infrastructure.persistence.calendario.jpa.FeriadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.calendario.mapper.CalendarioEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeriadoPersistenceAdapter implements FeriadoRepository {

    private final FeriadoJpaRepository   feriadoJpa;
    private final UsuarioAdminJpaRepository adminJpa;
    private final CalendarioEntityMapper mapper;

    @Override
    public Optional<Feriado> findById(Long id) {
        return feriadoJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Feriado> findByFecha(LocalDate fecha) {
        return feriadoJpa.findByFecha(fecha).map(mapper::toDomain);
    }

    @Override
    public List<Feriado> findByAnio(int anio) {
        return feriadoJpa.findByAnio(anio).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Feriado> findByFechaBetween(LocalDate inicio, LocalDate fin) {
        return feriadoJpa.findByFechaBetween(inicio, fin).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Feriado save(Feriado feriado) {
        var creador = feriado.getIdUsuarioCreador() != null
                ? adminJpa.findById(feriado.getIdUsuarioCreador()).orElse(null) : null;

        FeriadoEntity entity = FeriadoEntity.builder()
                .id(feriado.getId())
                .tipoFeriado(feriado.getTipoFeriado())
                .fecha(feriado.getFecha())
                .descripcion(feriado.getDescripcion())
                .anio(feriado.getFecha().getYear())
                .creadoPor(creador)
                .build();

        return mapper.toDomain(feriadoJpa.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        feriadoJpa.deleteById(id);
    }

    @Override
    public boolean existsByFecha(LocalDate fecha) {
        return feriadoJpa.existsByFecha(fecha);
    }
}