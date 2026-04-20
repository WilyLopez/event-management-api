package com.playzone.pems.infrastructure.persistence.facturacion.adapter;

import com.playzone.pems.domain.facturacion.model.Comprobante;
import com.playzone.pems.domain.facturacion.model.enums.EstadoComprobante;
import com.playzone.pems.domain.facturacion.repository.ComprobanteRepository;
import com.playzone.pems.infrastructure.persistence.facturacion.jpa.ComprobanteJpaRepository;
import com.playzone.pems.infrastructure.persistence.facturacion.jpa.SerieComprobanteJpaRepository;
import com.playzone.pems.infrastructure.persistence.facturacion.mapper.ComprobanteEntityMapper;
import com.playzone.pems.infrastructure.persistence.pago.jpa.PagoJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ComprobantePersistenceAdapter implements ComprobanteRepository {

    private final ComprobanteJpaRepository      comprobanteJpa;
    private final SerieComprobanteJpaRepository serieJpa;
    private final PagoJpaRepository             pagoJpa;
    private final ComprobanteEntityMapper       mapper;

    @Override public Optional<Comprobante> findById(Long id) {
        return comprobanteJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Optional<Comprobante> findByNumeroCompleto(String numero) {
        return comprobanteJpa.findByNumeroCompleto(numero).map(mapper::toDomain);
    }

    @Override public Optional<Comprobante> findByPago(Long idPago) {
        return comprobanteJpa.findByPago_Id(idPago).map(mapper::toDomain);
    }

    @Override public Page<Comprobante> findBySedeAndFechasBetween(Long idSede, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        return comprobanteJpa.findBySedeAndFechasBetween(idSede, desde, hasta, pageable).map(mapper::toDomain);
    }

    @Override public List<Comprobante> findPendientesDeEnvio() {
        return comprobanteJpa.findByEstadoComprobanteIn(
                        List.of(EstadoComprobante.PENDIENTE, EstadoComprobante.RECHAZADO))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Comprobante save(Comprobante comprobante) {
        var pago  = pagoJpa.findById(comprobante.getIdPago()).orElseThrow(() -> new ResourceNotFoundException("Pago", comprobante.getIdPago()));
        var serie = serieJpa.findById(comprobante.getIdSerie()).orElseThrow(() -> new ResourceNotFoundException("SerieComprobante", comprobante.getIdSerie()));
        var nota  = comprobante.getIdComprobanteNota() != null ? comprobanteJpa.findById(comprobante.getIdComprobanteNota()).orElse(null) : null;
        return mapper.toDomain(comprobanteJpa.save(mapper.toEntity(comprobante, pago, serie, nota)));
    }

    @Override public boolean existsByNumeroCompleto(String numero) {
        return comprobanteJpa.existsByNumeroCompleto(numero);
    }

}