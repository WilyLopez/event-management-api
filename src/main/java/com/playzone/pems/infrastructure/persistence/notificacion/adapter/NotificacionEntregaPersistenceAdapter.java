package com.playzone.pems.infrastructure.persistence.notificacion.adapter;

import com.playzone.pems.domain.notificacion.model.NotificacionEntrega;
import com.playzone.pems.domain.notificacion.repository.NotificacionEntregaRepository;
import com.playzone.pems.infrastructure.persistence.notificacion.jpa.NotificacionEntregaJpaRepository;
import com.playzone.pems.infrastructure.persistence.notificacion.jpa.NotificacionJpaRepository;
import com.playzone.pems.infrastructure.persistence.notificacion.mapper.NotificacionEntregaEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificacionEntregaPersistenceAdapter implements NotificacionEntregaRepository {

    private final NotificacionEntregaJpaRepository jpa;
    private final NotificacionJpaRepository        notificacionJpa;
    private final NotificacionEntregaEntityMapper  mapper;

    @Override
    @Transactional
    public NotificacionEntrega save(NotificacionEntrega entrega) {
        var notificacionRef = notificacionJpa.getReferenceById(entrega.getNotificacionId());
        return mapper.toDomain(jpa.save(mapper.toEntity(entrega, notificacionRef)));
    }

    @Override
    @Transactional
    public List<NotificacionEntrega> saveAll(List<NotificacionEntrega> entregas) {
        var entities = entregas.stream()
                .map(e -> mapper.toEntity(e, notificacionJpa.getReferenceById(e.getNotificacionId())))
                .toList();
        return jpa.saveAll(entities).stream().map(mapper::toDomain).toList();
    }
}
