package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.cms.dto.query.BannerQuery;
import com.playzone.pems.application.cms.port.in.GestionarBannerUseCase;
import com.playzone.pems.domain.cms.model.Banner;
import com.playzone.pems.domain.cms.repository.BannerRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BannerService implements GestionarBannerUseCase {

    private final BannerRepository   bannerRepository;
    private final SupabaseAuthFacade  authFacade;
    private final RegistrarLogUseCase auditoria;

    @Override
    @Transactional
    public BannerQuery crear(CrearCommand command) {
        validateFechas(command.fechaInicio(), command.fechaFin());
        Banner banner = Banner.builder()
                .idSede(command.idSede())
                .titulo(command.titulo())
                .descripcion(command.descripcion())
                .imagenUrl(command.imagenUrl())
                .imagenMovilUrl(command.imagenMovilUrl())
                .enlaceDestino(command.enlaceDestino())
                .textoBoton(command.textoBoton())
                .colorOverlay(command.colorOverlay())
                .tipoBanner(command.tipoBanner() != null ? command.tipoBanner() : "HOME")
                .fechaInicio(command.fechaInicio())
                .fechaFin(command.fechaFin())
                .activo(true)
                .orden(command.orden())
                .prioridad(command.prioridad())
                .soloMovil(command.soloMovil())
                .soloDesktop(command.soloDesktop())
                .idUsuarioCreador(command.idUsuario())
                .build();
        BannerQuery resultado = BannerQuery.from(bannerRepository.save(banner));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                command.idUsuario(),
                AuditoriaConstants.ACCION_CREAR, AuditoriaConstants.MOD_CMS,
                "Banner", resultado.getId(),
                null, resultado.getTitulo(),
                "Banner creado: " + resultado.getTitulo(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
    }

    @Override
    @Transactional
    public BannerQuery actualizar(ActualizarCommand command) {
        Banner existente = findOrThrow(command.idBanner());
        validateFechas(command.fechaInicio(), command.fechaFin());
        Banner actualizado = existente.toBuilder()
                .idSede(command.idSede())
                .titulo(command.titulo())
                .descripcion(command.descripcion())
                .imagenUrl(command.imagenUrl())
                .imagenMovilUrl(command.imagenMovilUrl())
                .enlaceDestino(command.enlaceDestino())
                .textoBoton(command.textoBoton())
                .colorOverlay(command.colorOverlay())
                .tipoBanner(command.tipoBanner() != null ? command.tipoBanner() : "HOME")
                .fechaInicio(command.fechaInicio())
                .fechaFin(command.fechaFin())
                .orden(command.orden())
                .prioridad(command.prioridad())
                .soloMovil(command.soloMovil())
                .soloDesktop(command.soloDesktop())
                .build();
        BannerQuery resultado = BannerQuery.from(bannerRepository.save(actualizado));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_CMS,
                "Banner", command.idBanner(),
                existente.getTitulo(), resultado.getTitulo(),
                "Banner actualizado: " + resultado.getTitulo(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BannerQuery> listar(Pageable pageable) {
        return bannerRepository.findAll(pageable).map(BannerQuery::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerQuery> listarPublicos(Long idSede) {
        return bannerRepository.findVisiblesBySedeAndFecha(idSede, LocalDate.now())
                .stream().map(BannerQuery::from).toList();
    }

    @Override
    @Transactional
    public void activar(Long idBanner) {
        Banner banner = findOrThrow(idBanner);
        bannerRepository.save(banner.toBuilder().activo(true).build());
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ACTIVAR, AuditoriaConstants.MOD_CMS,
                "Banner", idBanner,
                "inactivo", "activo",
                "Banner activado: " + banner.getTitulo(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
    }

    @Override
    @Transactional
    public void desactivar(Long idBanner) {
        Banner banner = findOrThrow(idBanner);
        bannerRepository.save(banner.toBuilder().activo(false).build());
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_DESACTIVAR, AuditoriaConstants.MOD_CMS,
                "Banner", idBanner,
                "activo", "inactivo",
                "Banner desactivado: " + banner.getTitulo(),
                null, null, AuditoriaConstants.NIVEL_WARNING, AuditoriaConstants.RESULTADO_EXITOSO));
    }

    @Override
    @Transactional
    public BannerQuery duplicar(Long idBanner, UUID idUsuario) {
        Banner original = findOrThrow(idBanner);
        Banner copia = Banner.builder()
                .idSede(original.getIdSede())
                .titulo(original.getTitulo() + " (copia)")
                .descripcion(original.getDescripcion())
                .imagenUrl(original.getImagenUrl())
                .imagenMovilUrl(original.getImagenMovilUrl())
                .enlaceDestino(original.getEnlaceDestino())
                .textoBoton(original.getTextoBoton())
                .colorOverlay(original.getColorOverlay())
                .tipoBanner(original.getTipoBanner())
                .fechaInicio(original.getFechaInicio())
                .fechaFin(original.getFechaFin())
                .activo(false)
                .orden(original.getOrden() + 1)
                .prioridad(original.getPrioridad())
                .soloMovil(original.isSoloMovil())
                .soloDesktop(original.isSoloDesktop())
                .idUsuarioCreador(idUsuario)
                .build();
        return BannerQuery.from(bannerRepository.save(copia));
    }

    @Override
    @Transactional
    public void reordenar(ReordenarCommand command) {
        AtomicInteger pos = new AtomicInteger(0);
        for (Long id : command.idsOrdenados()) {
            Banner b = findOrThrow(id);
            bannerRepository.save(b.toBuilder().orden(pos.getAndIncrement()).build());
        }
    }

    @Override
    @Transactional
    public void eliminar(Long idBanner) {
        Banner banner = findOrThrow(idBanner);
        bannerRepository.deleteById(idBanner);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ELIMINAR, AuditoriaConstants.MOD_CMS,
                "Banner", idBanner,
                banner.getTitulo(), null,
                "Banner eliminado: " + banner.getTitulo(),
                null, null, AuditoriaConstants.NIVEL_CRITICAL, AuditoriaConstants.RESULTADO_EXITOSO));
    }

    private Banner findOrThrow(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", id));
    }

    private void validateFechas(LocalDate inicio, LocalDate fin) {
        if (fin != null && fin.isBefore(inicio)) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la de inicio.");
        }
    }
}
