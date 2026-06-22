package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.MensajeContacto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MensajeContactoRepository {
    MensajeContacto save(MensajeContacto mensaje);
    Optional<MensajeContacto> findById(Long id);
    Page<MensajeContacto> findByEstado(String estado, Pageable pageable);
    Page<MensajeContacto> findAll(Pageable pageable);
    void deleteById(Long id);
}
