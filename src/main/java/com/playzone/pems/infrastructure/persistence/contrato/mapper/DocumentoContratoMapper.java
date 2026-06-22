package com.playzone.pems.infrastructure.persistence.contrato.mapper;

import com.playzone.pems.domain.contrato.model.DocumentoContrato;
import com.playzone.pems.domain.usuario.repository.PerfilUsuarioRepository;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import com.playzone.pems.infrastructure.persistence.contrato.entity.DocumentoContratoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentoContratoMapper {

    private final PerfilUsuarioRepository perfilUsuarioRepository;

    public DocumentoContrato toDomain(DocumentoContratoEntity e) {
        String nombreUsuarioCarga = e.getSubidoPor() != null
                ? perfilUsuarioRepository.buscarPorId(e.getSubidoPor())
                        .map(u -> u.getNombreCompleto()).orElse(null)
                : null;
        return DocumentoContrato.builder()
                .id(e.getId())
                .idContrato(e.getContrato().getId())
                .nombre(e.getNombre())
                .archivoUrl(e.getArchivoUrl())
                .tipoArchivo(e.getTipoArchivo())
                .tamanobytes(e.getTamanobytes())
                .idUsuarioCarga(e.getSubidoPor())
                .nombreUsuarioCarga(nombreUsuarioCarga)
                .fechaCarga(e.getFechaCarga())
                .build();
    }

    public DocumentoContratoEntity toEntity(DocumentoContrato d, ContratoEntity contrato) {
        return DocumentoContratoEntity.builder()
                .id(d.getId())
                .contrato(contrato)
                .nombre(d.getNombre())
                .archivoUrl(d.getArchivoUrl())
                .tipoArchivo(d.getTipoArchivo())
                .tamanobytes(d.getTamanobytes())
                .subidoPor(d.getIdUsuarioCarga())
                .build();
    }
}
