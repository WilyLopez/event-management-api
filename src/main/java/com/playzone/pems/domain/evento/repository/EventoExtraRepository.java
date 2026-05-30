package com.playzone.pems.domain.evento.repository;

import com.playzone.pems.domain.evento.model.EventoExtra;

import java.util.List;

public interface EventoExtraRepository {

    List<EventoExtra> findByEvento(Long idEventoPrivado);

    List<EventoExtra> saveAll(List<EventoExtra> extras);

    void deleteByEvento(Long idEventoPrivado);
}
