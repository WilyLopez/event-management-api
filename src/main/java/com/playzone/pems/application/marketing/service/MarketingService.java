package com.playzone.pems.application.marketing.service;

import com.playzone.pems.application.marketing.dto.command.CrearCampanaCommand;
import com.playzone.pems.application.marketing.dto.command.FiltroDestinatariosCommand;
import com.playzone.pems.application.marketing.dto.command.GuardarPlantillaCommand;
import com.playzone.pems.application.marketing.dto.query.CampanaEmailQuery;
import com.playzone.pems.application.marketing.dto.query.EnvioEmailQuery;
import com.playzone.pems.application.marketing.dto.query.PlantillaEmailQuery;
import com.playzone.pems.application.marketing.dto.query.TipoEmailQuery;
import com.playzone.pems.application.marketing.port.in.CrearCampanaEmailUseCase;
import com.playzone.pems.application.marketing.port.in.CrearPlantillaEmailUseCase;
import com.playzone.pems.application.marketing.port.in.EnviarCampanaUseCase;
import com.playzone.pems.application.marketing.port.in.ListarCampanasUseCase;
import com.playzone.pems.application.marketing.port.in.ListarEnviosUseCase;
import com.playzone.pems.application.marketing.port.in.ListarPlantillasUseCase;
import com.playzone.pems.domain.marketing.model.CampanaEmail;
import com.playzone.pems.domain.marketing.model.EnvioEmail;
import com.playzone.pems.domain.marketing.model.PlantillaEmail;
import com.playzone.pems.domain.marketing.repository.CampanaEmailRepository;
import com.playzone.pems.domain.marketing.repository.EnvioEmailRepository;
import com.playzone.pems.domain.marketing.repository.PlantillaEmailRepository;
import com.playzone.pems.domain.marketing.repository.TipoEmailRepository;
import com.playzone.pems.application.usuario.dto.query.CampanaDestinatariosQuery;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingService
        implements CrearPlantillaEmailUseCase,
        ListarPlantillasUseCase,
        CrearCampanaEmailUseCase,
        ListarCampanasUseCase,
        EnviarCampanaUseCase,
        ListarEnviosUseCase {

    private final PlantillaEmailRepository plantillaRepo;
    private final CampanaEmailRepository   campanaRepo;
    private final EnvioEmailRepository     envioRepo;
    private final TipoEmailRepository      tipoEmailRepo;
    private final ClientePerfilRepository   clientePerfilRepository;

    @Override
    @Transactional
    public PlantillaEmailQuery ejecutar(GuardarPlantillaCommand command, UUID idUsuarioEditor) {
        tipoEmailRepo.findById(command.getTipoEmailCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("TipoEmail", "codigo", command.getTipoEmailCodigo()));

        PlantillaEmail plantilla = PlantillaEmail.builder()
                .tipoEmailCodigo(command.getTipoEmailCodigo())
                .nombre(command.getNombre())
                .asunto(command.getAsunto())
                .contenidoHtml(command.getContenidoHtml())
                .contenidoFallback(command.getContenidoFallback())
                .variablesPermitidas(command.getVariablesPermitidas())
                .activa(true)
                .createdBy(idUsuarioEditor)
                .updatedBy(idUsuarioEditor)
                .fechaActualizacion(Instant.now())
                .build();

        return toPlantillaQuery(plantillaRepo.save(plantilla));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaEmailQuery> listarPlantillas(Pageable pageable) {
        return plantillaRepo.findAll(pageable)
                .getContent()
                .stream()
                .map(this::toPlantillaQuery)
                .toList();
    }

    @Override
    @Transactional
    public CampanaEmailQuery ejecutar(CrearCampanaCommand command, UUID idUsuarioCreador) {
        plantillaRepo.findById(command.getIdPlantillaEmail())
                .orElseThrow(() -> new ResourceNotFoundException("PlantillaEmail", command.getIdPlantillaEmail()));

        String estado = command.getFechaProgramada() != null ? "PROGRAMADA" : "BORRADOR";

        CampanaEmail campana = CampanaEmail.builder()
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .idPlantillaEmail(command.getIdPlantillaEmail())
                .estado(estado)
                .fechaProgramada(command.getFechaProgramada())
                .totalDestinatarios(0)
                .totalEnviados(0)
                .totalFallidos(0)
                .createdBy(idUsuarioCreador)
                .fechaCreacion(Instant.now())
                .build();

        return toCampanaQuery(campanaRepo.save(campana));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CampanaEmailQuery> listarCampanas(Pageable pageable) {
        Page<CampanaEmail> pagina = campanaRepo.findAll(pageable);
        return PagedResponse.<CampanaEmailQuery>builder()
                .content(pagina.getContent().stream().map(this::toCampanaQuery).toList())
                .page(pagina.getNumber())
                .size(pagina.getSize())
                .totalElements(pagina.getTotalElements())
                .totalPages(pagina.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public void ejecutar(Long idCampana, FiltroDestinatariosCommand filtro) {
        CampanaEmail campana = campanaRepo.findById(idCampana)
                .orElseThrow(() -> new ResourceNotFoundException("CampanaEmail", idCampana));

        if (!campana.estaPendiente()) {
            throw new ValidationException("estado", "La campaña no puede enviarse en su estado actual.");
        }

        PlantillaEmail plantilla = plantillaRepo.findById(campana.getIdPlantillaEmail())
                .orElseThrow(() -> new ResourceNotFoundException("PlantillaEmail", campana.getIdPlantillaEmail()));

        CampanaDestinatariosQuery filtroQuery = CampanaDestinatariosQuery.builder()
                .soloVip(filtro.getSoloVip())
                .soloFrecuentes(filtro.getSoloFrecuentes())
                .soloNuevos(filtro.getSoloNuevos())
                .soloInactivos(filtro.getSoloInactivos())
                .soloCorporativos(filtro.getSoloCorporativos())
                .soloPresenciales(filtro.getSoloPresenciales())
                .build();

        List<ClientePerfil> destinatarios = clientePerfilRepository.buscarDestinatariosCampana(filtroQuery);

        List<EnvioEmail> envios = destinatarios.stream()
                .filter(c -> c.getCorreo() != null)
                .map(c -> EnvioEmail.builder()
                        .idCampanaEmail(idCampana)
                        .idCliente(c.getId())
                        .destinatario(c.getCorreo())
                        .asunto(plantilla.getAsunto())
                        .estado("PENDIENTE")
                        .intentos(0)
                        .fechaCreacion(Instant.now())
                        .build())
                .toList();

        envioRepo.guardarTodos(envios);
        campanaRepo.actualizarEstado(idCampana, "ENVIANDO");

        log.info("Campaña {} preparada con {} destinatarios", idCampana, envios.size());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EnvioEmailQuery> ejecutar(Long idCampana, Pageable pageable) {
        Page<EnvioEmail> pagina = envioRepo.findByCampana(idCampana, pageable);
        return PagedResponse.<EnvioEmailQuery>builder()
                .content(pagina.getContent().stream().map(this::toEnvioQuery).toList())
                .page(pagina.getNumber())
                .size(pagina.getSize())
                .totalElements(pagina.getTotalElements())
                .totalPages(pagina.getTotalPages())
                .build();
    }

    public List<TipoEmailQuery> listarTipos() {
        return tipoEmailRepo.findAllActivos().stream()
                .map(t -> TipoEmailQuery.builder()
                        .codigo(t.getCodigo())
                        .nombre(t.getNombre())
                        .descripcion(t.getDescripcion())
                        .esSistema(t.isEsSistema())
                        .orden(t.getOrden())
                        .activo(t.isActivo())
                        .build())
                .toList();
    }

    private PlantillaEmailQuery toPlantillaQuery(PlantillaEmail p) {
        return PlantillaEmailQuery.builder()
                .id(p.getId())
                .tipoEmailCodigo(p.getTipoEmailCodigo())
                .tipoEmailNombre(tipoEmailRepo.findById(p.getTipoEmailCodigo())
                        .map(t -> t.getNombre())
                        .orElse(null))
                .nombre(p.getNombre())
                .asunto(p.getAsunto())
                .contenidoHtml(p.getContenidoHtml())
                .contenidoFallback(p.getContenidoFallback())
                .variablesPermitidas(p.getVariablesPermitidas())
                .activa(p.isActiva())
                .createdBy(p.getCreatedBy())
                .updatedBy(p.getUpdatedBy())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }

    private CampanaEmailQuery toCampanaQuery(CampanaEmail c) {
        return CampanaEmailQuery.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .idPlantillaEmail(c.getIdPlantillaEmail())
                .plantillaNombre(plantillaRepo.findById(c.getIdPlantillaEmail())
                        .map(p -> p.getNombre())
                        .orElse(null))
                .estado(c.getEstado())
                .fechaProgramada(c.getFechaProgramada())
                .totalDestinatarios(c.getTotalDestinatarios())
                .totalEnviados(c.getTotalEnviados())
                .totalFallidos(c.getTotalFallidos())
                .createdBy(c.getCreatedBy())
                .fechaCreacion(c.getFechaCreacion())
                .build();
    }

    private EnvioEmailQuery toEnvioQuery(EnvioEmail e) {
        return EnvioEmailQuery.builder()
                .id(e.getId())
                .idCampanaEmail(e.getIdCampanaEmail())
                .idCliente(e.getIdCliente())
                .destinatario(e.getDestinatario())
                .asunto(e.getAsunto())
                .estado(e.getEstado())
                .intentos(e.getIntentos())
                .fechaEnvio(e.getFechaEnvio())
                .mensajeError(e.getMensajeError())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
