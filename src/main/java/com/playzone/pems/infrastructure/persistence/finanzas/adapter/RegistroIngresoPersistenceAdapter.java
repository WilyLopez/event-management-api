package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.RegistroIngreso;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import com.playzone.pems.domain.finanzas.repository.RegistroIngresoRepository;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.RegistroIngresoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.TipoIngresoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.RegistroIngresoJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.TipoIngresoJpaRepository;
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
public class RegistroIngresoPersistenceAdapter implements RegistroIngresoRepository {

    private final RegistroIngresoJpaRepository  jpaRepository;
    private final TipoIngresoJpaRepository      tipoIngresoJpaRepository;
    private final SedeJpaRepository             sedeJpaRepository;
    private final ReservaPublicaJpaRepository   reservaPublicaJpaRepository;
    private final EventoPrivadoJpaRepository    eventoPrivadoJpaRepository;

    @Override
    public Optional<RegistroIngreso> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<RegistroIngreso> findBySede(Long idSede, Pageable pageable) {
        return jpaRepository.findBySede_IdWithTipo(idSede, pageable).map(this::toDomain);
    }

    @Override
    public List<RegistroIngreso> findBySedeAndRangoFecha(Long idSede, LocalDate inicio, LocalDate fin) {
        return jpaRepository.findBySede_IdAndFechaBetweenWithTipo(idSede, inicio, fin)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<RegistroIngreso> findBySedeAndPeriodo(Long idSede, int anio, int mes) {
        return jpaRepository.findBySede_IdAndPeriodoWithTipo(idSede, anio, mes)
                .stream().map(this::toDomain).toList();
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
        return jpaRepository.sumMontoBySedeAndPeriodoAndCategoria(idSede, anio, mes,
                CategoriaIngreso.valueOf(categoria));
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
    public RegistroIngreso save(RegistroIngreso ingreso) {
        TipoIngresoEntity tipo = tipoIngresoJpaRepository.getReferenceById(ingreso.getIdTipoIngreso());
        SedeEntity sede        = sedeJpaRepository.getReferenceById(ingreso.getIdSede());
        ReservaPublicaEntity reserva = ingreso.getIdReservaPublica() != null
                ? reservaPublicaJpaRepository.getReferenceById(ingreso.getIdReservaPublica()) : null;
        EventoPrivadoEntity evento = ingreso.getIdEventoPrivado() != null
                ? eventoPrivadoJpaRepository.getReferenceById(ingreso.getIdEventoPrivado()) : null;
        RegistroIngresoEntity entity = RegistroIngresoEntity.builder()
                .id(ingreso.getId())
                .tipoIngreso(tipo)
                .sede(sede)
                .reservaPublica(reserva)
                .eventoPrivado(evento)
                .monto(ingreso.getMonto())
                .fecha(ingreso.getFecha())
                .medioPago(ingreso.getMedioPago())
                .descripcion(ingreso.getDescripcion())
                .esAutomatico(ingreso.isEsAutomatico())
                .idUsuarioRegistra(ingreso.getIdUsuarioRegistra())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private RegistroIngreso toDomain(RegistroIngresoEntity e) {
        return RegistroIngreso.builder()
                .id(e.getId())
                .idTipoIngreso(e.getTipoIngreso().getId())
                .nombreTipoIngreso(e.getTipoIngreso().getNombre())
                .categoriaIngreso(e.getTipoIngreso().getCategoria())
                .idSede(e.getSede().getId())
                .idReservaPublica(e.getReservaPublica() != null ? e.getReservaPublica().getId() : null)
                .idEventoPrivado(e.getEventoPrivado() != null ? e.getEventoPrivado().getId() : null)
                .monto(e.getMonto())
                .fecha(e.getFecha())
                .medioPago(e.getMedioPago())
                .descripcion(e.getDescripcion())
                .esAutomatico(e.isEsAutomatico())
                .idUsuarioRegistra(e.getIdUsuarioRegistra())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
