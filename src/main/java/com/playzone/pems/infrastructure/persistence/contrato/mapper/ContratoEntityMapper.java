package com.playzone.pems.infrastructure.persistence.contrato.mapper;

import com.playzone.pems.domain.contrato.model.Contrato;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.domain.usuario.repository.PerfilUsuarioRepository;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ContratoEntityMapper {

    private final ClientePerfilRepository  clientePerfilRepository;
    private final PerfilUsuarioRepository  perfilUsuarioRepository;

    public Contrato toDomain(ContratoEntity e) {
        if (e == null) return null;
        var ev = e.getEventoPrivado();

        BigDecimal saldo = BigDecimal.ZERO;
        if (ev.getPrecioTotalContrato() != null) {
            saldo = ev.getPrecioTotalContrato().subtract(
                ev.getMontoAdelanto() != null ? ev.getMontoAdelanto() : BigDecimal.ZERO);
        }

        return Contrato.builder()
                .id(e.getId())
                .idEventoPrivado(ev.getId())
                .estado(e.getEstado())
                .contenidoTexto(e.getContenidoTexto())
                .archivoPdfUrl(e.getArchivoPdfUrl())
                .fechaFirma(e.getFechaFirma())
                .idUsuarioRedactor(e.getRedactorId())
                .usuarioRedactor(e.getRedactorId() != null
                        ? perfilUsuarioRepository.buscarPorId(e.getRedactorId())
                                .map(u -> u.getNombreCompleto()).orElse(null)
                        : null)
                .plantilla(e.getPlantilla())
                .observaciones(e.getObservaciones())
                .version(e.getVersion())
                .nombreCliente(clientePerfilRepository.buscarPorId(ev.getClienteId())
                        .map(ClientePerfil::nombreCompleto).orElse(null))
                .correoCliente(clientePerfilRepository.buscarPorId(ev.getClienteId())
                        .map(ClientePerfil::getCorreo).orElse(null))
                .tipoEvento(ev.getTipoEvento())
                .fechaEvento(ev.getFechaEvento())
                .turno(ev.getTurno().getNombre())
                .aforoDeclarado(ev.getAforoDeclarado())
                .precioTotalContrato(ev.getPrecioTotalContrato())
                .montoAdelanto(ev.getMontoAdelanto())
                .saldoPendiente(saldo)
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ContratoEntity toEntity(Contrato d, EventoPrivadoEntity evento) {
        if (d == null) return null;
        return ContratoEntity.builder()
                .id(d.getId())
                .eventoPrivado(evento)
                .estado(d.getEstado())
                .contenidoTexto(d.getContenidoTexto())
                .archivoPdfUrl(d.getArchivoPdfUrl())
                .fechaFirma(d.getFechaFirma())
                .redactorId(d.getIdUsuarioRedactor())
                .plantilla(d.getPlantilla())
                .observaciones(d.getObservaciones())
                .version(d.getVersion())
                .build();
    }
}
