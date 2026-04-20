package com.playzone.pems.application.proveedor.service;

import com.playzone.pems.application.proveedor.dto.command.GestionarProveedorCommand;
import com.playzone.pems.application.proveedor.dto.query.ProveedorQuery;
import com.playzone.pems.application.proveedor.port.in.GestionarProveedorUseCase;
import com.playzone.pems.application.proveedor.port.in.VincularProveedorContratoUseCase;
import com.playzone.pems.domain.contrato.model.ContratoProveedor;
import com.playzone.pems.domain.contrato.model.enums.ContratadoPor;
import com.playzone.pems.domain.contrato.repository.ContratoProveedorRepository;
import com.playzone.pems.domain.proveedor.exception.ProveedorNotFoundException;
import com.playzone.pems.domain.proveedor.model.Proveedor;
import com.playzone.pems.domain.proveedor.repository.ProveedorRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProveedorService
        implements GestionarProveedorUseCase,
        VincularProveedorContratoUseCase {

    private final ProveedorRepository         proveedorRepository;
    private final ContratoProveedorRepository contratoProveedorRepository;

    @Override
    @Transactional
    public ProveedorQuery crear(GestionarProveedorCommand command) {
        if (command.getRuc() != null && proveedorRepository.existsByRuc(command.getRuc())) {
            throw new ValidationException("ruc", "Ya existe un proveedor con ese RUC.");
        }

        Proveedor proveedor = buildDesdeCommand(command);
        return toQuery(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public ProveedorQuery actualizar(Long idProveedor, GestionarProveedorCommand command) {
        proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNotFoundException(idProveedor));

        Proveedor actualizado = buildDesdeCommand(command)
                .toBuilder()
                .id(idProveedor)
                .build();

        return toQuery(proveedorRepository.save(actualizado));
    }

    @Override
    @Transactional
    public void desactivar(Long idProveedor) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNotFoundException(idProveedor));
        proveedorRepository.save(proveedor.toBuilder().activo(false).build());
    }

    @Override
    @Transactional
    public ContratoProveedor vincular(Long idContrato, Long idProveedor,
                                      String servicioDescripcion, BigDecimal montoAcordado, ContratadoPor contratadoPor) {

        if (contratoProveedorRepository.existsByContratoAndProveedor(idContrato, idProveedor)) {
            throw new ValidationException("El proveedor ya está vinculado a ese contrato.");
        }

        ContratoProveedor vinculo = ContratoProveedor.builder()
                .idContrato(idContrato)
                .idProveedor(idProveedor)
                .servicioDescripcion(servicioDescripcion)
                .montoAcordado(montoAcordado)
                .contratadoPor(contratadoPor)
                .build();

        return contratoProveedorRepository.save(vinculo);
    }

    @Override
    @Transactional
    public void desvincular(Long idContratoProveedor) {
        contratoProveedorRepository.findById(idContratoProveedor)
                .orElseThrow(() -> new ResourceNotFoundException("ContratoProveedor", idContratoProveedor));
        contratoProveedorRepository.deleteById(idContratoProveedor);
    }

    private Proveedor buildDesdeCommand(GestionarProveedorCommand c) {
        return Proveedor.builder()
                .nombre(c.getNombre())
                .ruc(c.getRuc())
                .contactoNombre(c.getContactoNombre())
                .contactoTelefono(c.getContactoTelefono())
                .contactoCorreo(c.getContactoCorreo())
                .tipoServicio(c.getTipoServicio())
                .notas(c.getNotas())
                .activo(true)
                .build();
    }

    private ProveedorQuery toQuery(Proveedor p) {
        return ProveedorQuery.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .ruc(p.getRuc())
                .contactoNombre(p.getContactoNombre())
                .contactoTelefono(p.getContactoTelefono())
                .contactoCorreo(p.getContactoCorreo())
                .tipoServicio(p.getTipoServicio())
                .notas(p.getNotas())
                .activo(p.isActivo())
                .fechaCreacion(p.getFechaCreacion())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}