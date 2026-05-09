package com.playzone.pems.infrastructure.persistence.contrato.mapper;

import com.playzone.pems.domain.contrato.model.DocumentoContrato;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import com.playzone.pems.infrastructure.persistence.contrato.entity.DocumentoContratoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentoContratoMapper {

    public DocumentoContrato toDomain(DocumentoContratoEntity e) {
        return DocumentoContrato.builder()
                .id(e.getId())
                .idContrato(e.getContrato().getId())
                .nombre(e.getNombre())
                .archivoUrl(e.getArchivoUrl())
                .tipoArchivo(e.getTipoArchivo())
                .tamanobytes(e.getTamanobytes())
                .idUsuarioCarga(e.getUsuarioCarga().getId())
                .nombreUsuarioCarga(e.getUsuarioCarga().getNombre())
                .fechaCarga(e.getFechaCarga())
                .build();
    }

    public DocumentoContratoEntity toEntity(
            DocumentoContrato d,
            ContratoEntity contrato,
            UsuarioAdminEntity usuario) {
        return DocumentoContratoEntity.builder()
                .id(d.getId())
                .contrato(contrato)
                .nombre(d.getNombre())
                .archivoUrl(d.getArchivoUrl())
                .tipoArchivo(d.getTipoArchivo())
                .tamanobytes(d.getTamanobytes())
                .usuarioCarga(usuario)
                .build();
    }
}