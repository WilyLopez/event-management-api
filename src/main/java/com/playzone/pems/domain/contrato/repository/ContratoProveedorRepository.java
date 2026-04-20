package com.playzone.pems.domain.contrato.repository;

import com.playzone.pems.domain.contrato.model.ContratoProveedor;

import java.util.List;
import java.util.Optional;

public interface ContratoProveedorRepository {

    Optional<ContratoProveedor> findById(Long id);

    List<ContratoProveedor> findByContrato(Long idContrato);

    ContratoProveedor save(ContratoProveedor contratoProveedor);

    void deleteById(Long id);

    boolean existsByContratoAndProveedor(Long idContrato, Long idProveedor);
}