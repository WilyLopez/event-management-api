package com.playzone.pems.infrastructure.persistence.contrato.adapter;

import com.playzone.pems.domain.contrato.model.DocumentoContrato;
import com.playzone.pems.domain.contrato.repository.DocumentoContratoRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.ContratoJpaRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.DocumentoContratoJpaRepository;
import com.playzone.pems.infrastructure.persistence.contrato.mapper.DocumentoContratoMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentoContratoPersistenceAdapter implements DocumentoContratoRepository {

    private final DocumentoContratoJpaRepository jpa;
    private final ContratoJpaRepository          contratoJpa;
    private final UsuarioAdminJpaRepository      adminJpa;
    private final DocumentoContratoMapper        mapper;

    @Override
    public Optional<DocumentoContrato> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<DocumentoContrato> findByContrato(Long idContrato) {
        return jpa.findByContrato_Id(idContrato)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public DocumentoContrato save(DocumentoContrato documento) {
        var contrato = contratoJpa.findById(documento.getIdContrato())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrato", documento.getIdContrato()));
        var usuario = adminJpa.findById(documento.getIdUsuarioCarga())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UsuarioAdmin", documento.getIdUsuarioCarga()));
        return mapper.toDomain(jpa.save(mapper.toEntity(documento, contrato, usuario)));
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}