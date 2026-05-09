package com.playzone.pems.domain.contrato.repository;

import com.playzone.pems.domain.contrato.model.DocumentoContrato;

import java.util.List;
import java.util.Optional;

public interface DocumentoContratoRepository {

    Optional<DocumentoContrato> findById(Long id);

    List<DocumentoContrato> findByContrato(Long idContrato);

    DocumentoContrato save(DocumentoContrato documento);

    void deleteById(Long id);
}