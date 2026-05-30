package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.RegistroEgreso;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import com.playzone.pems.domain.finanzas.repository.RegistroEgresoRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.RegistroEgresoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.TipoEgresoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.RegistroEgresoJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.TipoEgresoJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.mapper.FinanzasEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistroEgresoPersistenceAdapter implements RegistroEgresoRepository {

    private final RegistroEgresoJpaRepository jpaRepository;
    private final TipoEgresoJpaRepository     tipoEgresoJpaRepository;
    private final SedeJpaRepository           sedeJpaRepository;
    private final FinanzasEntityMapper        mapper;

    @Override
    public Optional<RegistroEgreso> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<RegistroEgreso> findBySede(Long idSede, Pageable pageable) {
        return jpaRepository.findBySede_IdWithTipo(idSede, pageable).map(mapper::toDomain);
    }

    @Override
    public List<RegistroEgreso> findBySedeAndPeriodo(Long idSede, int anio, int mes) {
        return jpaRepository.findBySede_IdAndPeriodoWithTipo(idSede, anio, mes)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistroEgreso> findBySedeAndRangoFecha(Long idSede, LocalDate inicio, LocalDate fin) {
        return jpaRepository.findBySede_IdAndFechaBetweenWithTipo(idSede, inicio, fin)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public BigDecimal sumMontoBySedeAndPeriodo(Long idSede, int anio, int mes) {
        return jpaRepository.sumMontoBySedeAndPeriodo(idSede, anio, mes);
    }

    @Override
    public BigDecimal sumMontoBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return jpaRepository.sumMontoBySedeAndRango(idSede, inicio, fin);
    }

    @Override
    public BigDecimal sumMontoBySedeAndPeriodoAndCategoria(Long idSede, int anio, int mes, String categoria) {
        return jpaRepository.sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes, CategoriaEgreso.valueOf(categoria));
    }

    @Override
    public Map<Long, BigDecimal> sumMontoAgrupadoPorTipo(Long idSede, int anio, int mes) {
        return jpaRepository.sumMontoAgrupadoPorTipo(idSede, anio, mes).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    @Override
    @Transactional
    public RegistroEgreso save(RegistroEgreso registro) {
        TipoEgresoEntity tipo = tipoEgresoJpaRepository.getReferenceById(registro.getIdTipoEgreso());
        SedeEntity sede       = sedeJpaRepository.getReferenceById(registro.getIdSede());
        RegistroEgresoEntity entity = RegistroEgresoEntity.builder()
                .id(registro.getId())
                .tipoEgreso(tipo)
                .sede(sede)
                .monto(registro.getMonto())
                .fecha(registro.getFecha())
                .periodoAnio(registro.getPeriodoAnio())
                .periodoMes(registro.getPeriodoMes())
                .descripcion(registro.getDescripcion())
                .comprobanteUrl(registro.getComprobanteUrl())
                .esRecurrente(registro.isEsRecurrente())
                .idUsuarioRegistra(registro.getIdUsuarioRegistra())
                .build();
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
