package com.playzone.pems.application.notificacion.service;

import com.playzone.pems.application.notificacion.dto.command.CrearNotificacionCommand;
import com.playzone.pems.application.notificacion.dto.query.NotificacionQuery;
import com.playzone.pems.application.notificacion.port.in.CrearNotificacionUseCase;
import com.playzone.pems.application.notificacion.port.in.MarcarNotificacionLeidaUseCase;
import com.playzone.pems.application.notificacion.port.in.ObtenerNotificacionesUseCase;
import com.playzone.pems.application.notificacion.port.out.CrearNotificacionPort;
import com.playzone.pems.domain.notificacion.model.Notificacion;
import com.playzone.pems.domain.notificacion.model.NotificacionEntrega;
import com.playzone.pems.domain.notificacion.model.TipoNotificacion;
import com.playzone.pems.domain.notificacion.repository.NotificacionEntregaRepository;
import com.playzone.pems.domain.notificacion.repository.NotificacionRepository;
import com.playzone.pems.domain.notificacion.repository.TipoNotificacionRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificacionService
        implements CrearNotificacionUseCase,
                   CrearNotificacionPort,
                   ObtenerNotificacionesUseCase,
                   MarcarNotificacionLeidaUseCase {

    private final TipoNotificacionRepository tipoRepo;
    private final NotificacionRepository     notifRepo;
    private final NotificacionEntregaRepository entregaRepo;

    @Override
    @Async("asyncExecutor")
    @Transactional
    public void notificar(CrearNotificacionCommand cmd) {
        try {
            persistir(cmd);
        } catch (Exception e) {
            log.error("Error al crear notificacion tipo={} destinatarioUsuario={} destinatarioCliente={}",
                    cmd.getTipoCodigo(), cmd.getDestinatarioUsuarioId(), cmd.getDestinatarioClienteId(), e);
        }
    }

    @Override
    @Transactional
    public NotificacionQuery ejecutar(CrearNotificacionCommand cmd) {
        return persistir(cmd);
    }

    @Override
    public Page<NotificacionQuery> feedUsuario(UUID usuarioId, boolean soloNoLeidas, Pageable pageable) {
        return notifRepo.findFeedUsuario(usuarioId, soloNoLeidas, pageable).map(this::toQuery);
    }

    @Override
    public Page<NotificacionQuery> feedCliente(Long clienteId, boolean soloNoLeidas, Pageable pageable) {
        return notifRepo.findFeedCliente(clienteId, soloNoLeidas, pageable).map(this::toQuery);
    }

    @Override
    public long contarNoLeidasUsuario(UUID usuarioId) {
        return notifRepo.contarNoLeidasUsuario(usuarioId);
    }

    @Override
    public long contarNoLeidasCliente(Long clienteId) {
        return notifRepo.contarNoLeidasCliente(clienteId);
    }

    @Override
    @Transactional
    public void marcarLeidaUsuario(Long id, UUID usuarioId) {
        notifRepo.marcarLeidaUsuario(id, usuarioId);
    }

    @Override
    @Transactional
    public void marcarLeidaCliente(Long id, Long clienteId) {
        notifRepo.marcarLeidaCliente(id, clienteId);
    }

    @Override
    @Transactional
    public void marcarTodasLeidasUsuario(UUID usuarioId) {
        notifRepo.marcarTodasLeidasUsuario(usuarioId);
    }

    @Override
    @Transactional
    public void marcarTodasLeidasCliente(Long clienteId) {
        notifRepo.marcarTodasLeidasCliente(clienteId);
    }

    private NotificacionQuery persistir(CrearNotificacionCommand cmd) {
        TipoNotificacion tipo = tipoRepo.findByCodigo(cmd.getTipoCodigo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TipoNotificacion", "codigo", cmd.getTipoCodigo()));

        String titulo  = interpolar(tipo.getPlantillaTitulo(), cmd.getDatosExtra());
        String mensaje = interpolar(tipo.getPlantillaMensaje(), cmd.getDatosExtra());

        Notificacion notif = Notificacion.builder()
                .tipoCodigo(tipo.getCodigo())
                .destinatarioUsuarioId(cmd.getDestinatarioUsuarioId())
                .destinatarioClienteId(cmd.getDestinatarioClienteId())
                .entidadTipo(cmd.getEntidadTipo())
                .entidadId(cmd.getEntidadId())
                .titulo(titulo)
                .mensaje(mensaje)
                .urlAccion(cmd.getUrlAccion())
                .metadata(cmd.getMetadata())
                .prioridad(tipo.getPrioridad())
                .expiraAt(calcularExpiracion(tipo.getPrioridad()))
                .leida(false)
                .build();

        Notificacion guardada = notifRepo.save(notif);

        registrarEntregas(guardada.getId(), tipo.getCanalesDefault());

        return toQuery(guardada);
    }

    private void registrarEntregas(Long notificacionId, List<String> canales) {
        if (canales == null || canales.isEmpty()) return;

        List<NotificacionEntrega> entregas = new ArrayList<>();

        for (String canal : canales) {
            String estado = canal.equals("IN_APP") ? "ENVIADO" : "PENDIENTE";
            entregas.add(NotificacionEntrega.builder()
                    .notificacionId(notificacionId)
                    .canal(canal)
                    .estado(estado)
                    .intentos(0)
                    .enviadoAt(canal.equals("IN_APP") ? OffsetDateTime.now() : null)
                    .build());
        }

        entregaRepo.saveAll(entregas);
    }

    private String interpolar(String plantilla, Map<String, String> datos) {
        if (plantilla == null) return null;
        if (datos == null || datos.isEmpty()) return plantilla;
        String resultado = plantilla;
        for (var entrada : datos.entrySet()) {
            resultado = resultado.replace(
                    "{" + entrada.getKey() + "}",
                    entrada.getValue() != null ? entrada.getValue() : "");
        }
        return resultado;
    }

    private OffsetDateTime calcularExpiracion(String prioridad) {
        return switch (prioridad) {
            case "BAJA"    -> OffsetDateTime.now().plusDays(7);
            case "ALTA"    -> OffsetDateTime.now().plusDays(60);
            case "CRITICA" -> OffsetDateTime.now().plusDays(90);
            default        -> OffsetDateTime.now().plusDays(30);
        };
    }

    private NotificacionQuery toQuery(Notificacion n) {
        return NotificacionQuery.builder()
                .id(n.getId())
                .tipoCodigo(n.getTipoCodigo())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .prioridad(n.getPrioridad())
                .urlAccion(n.getUrlAccion())
                .leida(n.isLeida())
                .leidaAt(n.getLeidaAt())
                .expiraAt(n.getExpiraAt())
                .createdAt(n.getCreatedAt())
                .entidadTipo(n.getEntidadTipo())
                .entidadId(n.getEntidadId())
                .build();
    }
}
