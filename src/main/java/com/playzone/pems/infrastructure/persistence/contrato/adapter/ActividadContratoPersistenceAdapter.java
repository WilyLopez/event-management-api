package com.playzone.pems.infrastructure.persistence.contrato.adapter;

import com.playzone.pems.domain.contrato.model.ActividadContrato;
import com.playzone.pems.domain.contrato.repository.ActividadContratoRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.ActividadContratoJpaRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.ContratoJpaRepository;
import com.playzone.pems.infrastructure.persistence.contrato.mapper.ActividadContratoMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActividadContratoPersistenceAdapter implements ActividadContratoRepository {

    private final ActividadContratoJpaRepository jpa;
    private final ContratoJpaRepository          contratoJpa;
    private final UsuarioAdminJpaRepository      adminJpa;
    private final ActividadContratoMapper        mapper;

    @Override
    public List<ActividadContrato> findByContrato(Long idContrato) {
        return jpa.findByContrato_IdOrderByFechaAccionDesc(idContrato)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public ActividadContrato save(ActividadContrato actividad) {
        var contrato = contratoJpa.findById(actividad.getIdContrato())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrato", actividad.getIdContrato()));

        var usuario = actividad.getIdUsuario() != null
                ? adminJpa.findById(actividad.getIdUsuario()).orElse(null)
                : null;

        return mapper.toDomain(jpa.save(mapper.toEntity(actividad, contrato, usuario)));
    }
}