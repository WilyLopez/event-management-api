package com.playzone.pems.infrastructure.persistence.calendario.adapter;

import com.playzone.pems.domain.calendario.model.Feriado;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.infrastructure.persistence.calendario.entity.FeriadoEntity;
import com.playzone.pems.infrastructure.persistence.calendario.jpa.FeriadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.calendario.mapper.CalendarioEntityMapper;
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
    private final CalendarioEntityMapper mapper;

    @Override
    public Optional<Feriado> findById(Long id) {
        return feriadoJpa.findById(id).map(mapper::toDomain);
    }

    @org.springframework.cache.annotation.Cacheable(value = "feriados", key = "#fecha")
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

    @org.springframework.cache.annotation.CacheEvict(value = "feriados", allEntries = true)
    @Override
    @Transactional
    public Feriado save(Feriado feriado) {
        FeriadoEntity entity = FeriadoEntity.builder()
                .id(feriado.getId())
                .tipoFeriado(feriado.getTipoFeriado())
                .fecha(feriado.getFecha())
                .descripcion(feriado.getDescripcion())
                .anio(feriado.getFecha().getYear())
                .build();

        return mapper.toDomain(feriadoJpa.save(entity));
    }

    @org.springframework.cache.annotation.CacheEvict(value = "feriados", allEntries = true)
    @Override
    public void deleteById(Long id) {
        feriadoJpa.deleteById(id);
    }

    @Override
    public boolean existsByFecha(LocalDate fecha) {
        return feriadoJpa.existsByFecha(fecha);
    }
}