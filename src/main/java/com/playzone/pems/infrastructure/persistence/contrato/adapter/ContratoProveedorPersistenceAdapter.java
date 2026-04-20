package com.playzone.pems.infrastructure.persistence.contrato.adapter;

import com.playzone.pems.domain.contrato.model.ContratoProveedor;
import com.playzone.pems.domain.contrato.repository.ContratoProveedorRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.ContratoJpaRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.ContratoProveedorJpaRepository;
import com.playzone.pems.infrastructure.persistence.contrato.mapper.ContratoEntityMapper;
import com.playzone.pems.infrastructure.persistence.proveedor.jpa.ProveedorJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContratoProveedorPersistenceAdapter implements ContratoProveedorRepository {

    private final ContratoProveedorJpaRepository cpJpa;
    private final ContratoJpaRepository          contratoJpa;
    private final ProveedorJpaRepository         proveedorJpa;
    private final ContratoEntityMapper           mapper;

    @Override
    public Optional<ContratoProveedor> findById(Long id) {
        return cpJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ContratoProveedor> findByContrato(Long idContrato) {
        return cpJpa.findByContrato_Id(idContrato).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public ContratoProveedor save(ContratoProveedor cp) {
        var contrato = contratoJpa.findById(cp.getIdContrato())
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", cp.getIdContrato()));
        var proveedor = proveedorJpa.findById(cp.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", cp.getIdProveedor()));
        return mapper.toDomain(cpJpa.save(mapper.toEntity(cp, contrato, proveedor)));
    }

    @Override
    public void deleteById(Long id) {
        cpJpa.deleteById(id);
    }

    @Override
    public boolean existsByContratoAndProveedor(Long idContrato, Long idProveedor) {
        return cpJpa.existsByContrato_IdAndProveedor_Id(idContrato, idProveedor);
    }
}