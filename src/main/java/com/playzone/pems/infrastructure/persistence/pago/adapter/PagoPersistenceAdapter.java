package com.playzone.pems.infrastructure.persistence.pago.adapter;

import com.playzone.pems.domain.pago.model.Pago;
import com.playzone.pems.domain.pago.repository.PagoRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import com.playzone.pems.infrastructure.persistence.pago.jpa.PagoJpaRepository;
import com.playzone.pems.infrastructure.persistence.pago.mapper.PagoEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
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
public class PagoPersistenceAdapter implements PagoRepository {

    private final PagoJpaRepository          pagoJpa;
    private final ReservaPublicaJpaRepository reservaJpa;
    private final EventoPrivadoJpaRepository  eventoJpa;
    private final UsuarioAdminJpaRepository   adminJpa;
    private final PagoEntityMapper           mapper;

    @Override public Optional<Pago> findById(Long id) {
        return pagoJpa.findById(id).map(mapper::toDomain);
    }

    @Override public List<Pago> findByReservaPublica(Long id) {
        return pagoJpa.findByReservaPublica_Id(id).stream().map(mapper::toDomain).toList();
    }

    @Override public List<Pago> findByEventoPrivado(Long id) {
        return pagoJpa.findByEventoPrivado_Id(id).stream().map(mapper::toDomain).toList();
    }

    @Override public List<Pago> findByVenta(Long idVenta) {
        return pagoJpa.findByIdVenta(idVenta).stream().map(mapper::toDomain).toList();
    }

    @Override public Page<Pago> findBySedeAndFechasBetween(Long idSede, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        return pagoJpa.findBySedeAndFechasBetween(idSede, desde, hasta, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Pago save(Pago pago) {
        var reserva  = pago.getIdReservaPublica() != null ? reservaJpa.findById(pago.getIdReservaPublica()).orElse(null) : null;
        var evento   = pago.getIdEventoPrivado()  != null ? eventoJpa.findById(pago.getIdEventoPrivado()).orElse(null)  : null;
        var usuario  = pago.getIdUsuarioRegistra() != null ? adminJpa.findById(pago.getIdUsuarioRegistra()).orElse(null) : null;
        return mapper.toDomain(pagoJpa.save(mapper.toEntity(pago, reserva, evento, usuario)));
    }
}