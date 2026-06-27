package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.GastoEventoPrivado;
import com.playzone.pems.domain.finanzas.repository.GastoEventoPrivadoRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.GastoEventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.GastoEventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.mapper.FinanzasEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GastoEventoPrivadoPersistenceAdapter implements GastoEventoPrivadoRepository {

    private final GastoEventoPrivadoJpaRepository jpaRepository;
    private final FinanzasEntityMapper            mapper;

    @Override
    public List<GastoEventoPrivado> findByEvento(Long idEvento) {
        return jpaRepository.findByEventoId(idEvento).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<GastoEventoPrivado> findBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return jpaRepository.findBySedeAndRangoConFecha(idSede, inicio, fin)
                .stream().map(row -> {
                    GastoEventoPrivadoEntity entity = (GastoEventoPrivadoEntity) row[0];
                    LocalDate fechaEvento = (LocalDate) row[1];
                    return mapper.toDomain(entity).toBuilder()
                            .fechaEvento(fechaEvento)
                            .build();
                }).toList();
    }

    @Override
    public BigDecimal sumMontoByEvento(Long idEvento) {
        return jpaRepository.sumMontoByEvento(idEvento);
    }

    @Override
    public Map<Long, BigDecimal> sumMontoByEventoIds(List<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return jpaRepository.sumMontoByEventoIds(ids).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    @Override
    @Transactional
    public GastoEventoPrivado save(GastoEventoPrivado gasto) {
        GastoEventoPrivadoEntity entity = GastoEventoPrivadoEntity.builder()
                .id(gasto.getId())
                .eventoId(gasto.getIdEventoPrivado())
                .descripcion(gasto.getDescripcion())
                .monto(gasto.getMonto())
                .comprobantePath(gasto.getComprobanteUrl())
                .createdBy(gasto.getIdUsuarioRegistra())
                .build();
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
