package com.playzone.pems.infrastructure.persistence.facturacion.adapter;

import com.playzone.pems.domain.facturacion.model.SerieComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.domain.facturacion.repository.SerieComprobanteRepository;
import com.playzone.pems.infrastructure.persistence.facturacion.jpa.SerieComprobanteJpaRepository;
import com.playzone.pems.infrastructure.persistence.facturacion.mapper.ComprobanteEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SerieComprobantePersistenceAdapter implements SerieComprobanteRepository {

    private final SerieComprobanteJpaRepository serieJpa;
    private final SedeJpaRepository             sedeJpa;
    private final ComprobanteEntityMapper       mapper;

    @Override
    public Optional<SerieComprobante> findById(Long id) {
        return serieJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SerieComprobante> findActivaBySedeAndTipo(Long idSede, TipoComprobante tipo) {
        return serieJpa.findBySede_IdAndTipoComprobanteAndActivoTrue(idSede, tipo).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public SerieComprobante save(SerieComprobante serie) {
        var sede = sedeJpa.findById(serie.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", serie.getIdSede()));
        return mapper.toDomain(serieJpa.save(mapper.toEntity(serie, sede)));
    }

    @Override
    @Transactional
    public int incrementarCorrelativoYRetornar(Long idSerie) {
        serieJpa.incrementarCorrelativo(idSerie);
        return serieJpa.findCorrelativoActual(idSerie);
    }
}