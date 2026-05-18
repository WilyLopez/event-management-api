package com.playzone.pems.application.preferencia.service;

import com.playzone.pems.application.preferencia.port.in.PreferenciaAdminUseCase;
import com.playzone.pems.domain.preferencia.model.PreferenciaAdmin;
import com.playzone.pems.domain.preferencia.repository.PreferenciaAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenciaAdminService implements PreferenciaAdminUseCase {

    private final PreferenciaAdminRepository repository;
    private PreferenciaAdminService self;

    @Autowired
    public void setSelf(@Lazy PreferenciaAdminService self) {
        this.self = self;
    }

    @Override
    @Transactional
    public PreferenciaAdmin getOrCreate(Long idUsuarioAdmin) {
        return repository.findByIdUsuarioAdmin(idUsuarioAdmin)
                .orElseGet(() -> {
                    try {
                        return self.createDefaultPreferences(idUsuarioAdmin);
                    } catch (DataIntegrityViolationException e) {
                        // Si falla por concurrencia, intentamos recuperar la que ya existe
                        return repository.findByIdUsuarioAdmin(idUsuarioAdmin)
                                .orElseThrow(() -> e);
                    }
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PreferenciaAdmin createDefaultPreferences(Long idUsuarioAdmin) {
        return repository.save(PreferenciaAdmin.defaultsFor(idUsuarioAdmin));
    }

    @Override
    @Transactional
    public PreferenciaAdmin update(Long idUsuarioAdmin, UpdateCommand cmd) {
        PreferenciaAdmin existing = getOrCreate(idUsuarioAdmin);
        PreferenciaAdmin updated = existing.toBuilder()
                .tema(cmd.getTema())
                .colorPrimario(cmd.getColorPrimario())
                .colorSecundario(cmd.getColorSecundario())
                .colorSidebar(cmd.getColorSidebar())
                .colorAcento(cmd.getColorAcento())
                .tipografia(cmd.getTipografia())
                .tamanioFuente(cmd.getTamanioFuente())
                .radiosBordes(cmd.getRadiosBordes())
                .sidebarColapsado(cmd.isSidebarColapsado())
                .sidebarFlotante(cmd.isSidebarFlotante())
                .modoCompacto(cmd.isModoCompacto())
                .anchoContenido(cmd.getAnchoContenido())
                .mostrarMigaspan(cmd.isMostrarMigaspan())
                .mostrarIconosMenu(cmd.isMostrarIconosMenu())
                .mostrarAnimaciones(cmd.isMostrarAnimaciones())
                .animacionSidebar(cmd.isAnimacionSidebar())
                .hoverEffects(cmd.isHoverEffects())
                .loadersAnimados(cmd.isLoadersAnimados())
                .confirmarAcciones(cmd.isConfirmarAcciones())
                .recordarUltimaPagina(cmd.isRecordarUltimaPagina())
                .restaurarTabs(cmd.isRestaurarTabs())
                .autoRefreshDashboard(cmd.isAutoRefreshDashboard())
                .intervaloRefreshSeg(cmd.getIntervaloRefreshSeg())
                .dashboardPersonalizado(cmd.getDashboardPersonalizado())
                .widgetsVisibles(cmd.getWidgetsVisibles())
                .accesosRapidos(cmd.getAccesosRapidos())
                .ordenWidgets(cmd.getOrdenWidgets())
                .layoutDashboard(cmd.getLayoutDashboard())
                .filtrosPersistentes(cmd.getFiltrosPersistentes())
                .columnasVisibles(cmd.getColumnasVisibles())
                .ordenamientoTablas(cmd.getOrdenamientoTablas())
                .elementosPorTabla(cmd.getElementosPorTabla())
                .sonidoNotificaciones(cmd.isSonidoNotificaciones())
                .notificacionesPush(cmd.isNotificacionesPush())
                .notificacionesEmail(cmd.isNotificacionesEmail())
                .notificacionesVisuales(cmd.isNotificacionesVisuales())
                .badgesDinamicos(cmd.isBadgesDinamicos())
                .idioma(cmd.getIdioma())
                .zonaHoraria(cmd.getZonaHoraria())
                .formatoFecha(cmd.getFormatoFecha())
                .formatoHora(cmd.getFormatoHora())
                .primerDiaSemana(cmd.getPrimerDiaSemana())
                .altoContraste(cmd.isAltoContraste())
                .reducirAnimaciones(cmd.isReducirAnimaciones())
                .aumentarEspaciado(cmd.isAumentarEspaciado())
                .cursorGrande(cmd.isCursorGrande())
                .build();
        return repository.save(updated);
    }

    @Override
    @Transactional
    public PreferenciaAdmin patch(Long idUsuarioAdmin, PatchCommand cmd) {
        PreferenciaAdmin existing = getOrCreate(idUsuarioAdmin);
        PreferenciaAdmin.PreferenciaAdminBuilder builder = existing.toBuilder();

        if (cmd.getTema()                != null) builder.tema(cmd.getTema());
        if (cmd.getColorPrimario()       != null) builder.colorPrimario(cmd.getColorPrimario());
        if (cmd.getColorSecundario()     != null) builder.colorSecundario(cmd.getColorSecundario());
        if (cmd.getColorSidebar()        != null) builder.colorSidebar(cmd.getColorSidebar());
        if (cmd.getColorAcento()         != null) builder.colorAcento(cmd.getColorAcento());
        if (cmd.getTipografia()          != null) builder.tipografia(cmd.getTipografia());
        if (cmd.getTamanioFuente()       != null) builder.tamanioFuente(cmd.getTamanioFuente());
        if (cmd.getRadiosBordes()        != null) builder.radiosBordes(cmd.getRadiosBordes());
        if (cmd.getSidebarColapsado()    != null) builder.sidebarColapsado(cmd.getSidebarColapsado());
        if (cmd.getSidebarFlotante()     != null) builder.sidebarFlotante(cmd.getSidebarFlotante());
        if (cmd.getModoCompacto()        != null) builder.modoCompacto(cmd.getModoCompacto());
        if (cmd.getAnchoContenido()      != null) builder.anchoContenido(cmd.getAnchoContenido());
        if (cmd.getMostrarMigaspan()     != null) builder.mostrarMigaspan(cmd.getMostrarMigaspan());
        if (cmd.getMostrarIconosMenu()   != null) builder.mostrarIconosMenu(cmd.getMostrarIconosMenu());
        if (cmd.getMostrarAnimaciones()  != null) builder.mostrarAnimaciones(cmd.getMostrarAnimaciones());
        if (cmd.getAnimacionSidebar()    != null) builder.animacionSidebar(cmd.getAnimacionSidebar());
        if (cmd.getHoverEffects()        != null) builder.hoverEffects(cmd.getHoverEffects());
        if (cmd.getLoadersAnimados()     != null) builder.loadersAnimados(cmd.getLoadersAnimados());
        if (cmd.getConfirmarAcciones()   != null) builder.confirmarAcciones(cmd.getConfirmarAcciones());
        if (cmd.getRecordarUltimaPagina()!= null) builder.recordarUltimaPagina(cmd.getRecordarUltimaPagina());
        if (cmd.getRestaurarTabs()       != null) builder.restaurarTabs(cmd.getRestaurarTabs());
        if (cmd.getAutoRefreshDashboard()!= null) builder.autoRefreshDashboard(cmd.getAutoRefreshDashboard());
        if (cmd.getIntervaloRefreshSeg() != null) builder.intervaloRefreshSeg(cmd.getIntervaloRefreshSeg());
        if (cmd.getDashboardPersonalizado() != null) builder.dashboardPersonalizado(cmd.getDashboardPersonalizado());
        if (cmd.getWidgetsVisibles()     != null) builder.widgetsVisibles(cmd.getWidgetsVisibles());
        if (cmd.getAccesosRapidos()      != null) builder.accesosRapidos(cmd.getAccesosRapidos());
        if (cmd.getOrdenWidgets()        != null) builder.ordenWidgets(cmd.getOrdenWidgets());
        if (cmd.getLayoutDashboard()     != null) builder.layoutDashboard(cmd.getLayoutDashboard());
        if (cmd.getFiltrosPersistentes() != null) builder.filtrosPersistentes(cmd.getFiltrosPersistentes());
        if (cmd.getColumnasVisibles()    != null) builder.columnasVisibles(cmd.getColumnasVisibles());
        if (cmd.getOrdenamientoTablas()  != null) builder.ordenamientoTablas(cmd.getOrdenamientoTablas());
        if (cmd.getElementosPorTabla()   != null) builder.elementosPorTabla(cmd.getElementosPorTabla());
        if (cmd.getSonidoNotificaciones()!= null) builder.sonidoNotificaciones(cmd.getSonidoNotificaciones());
        if (cmd.getNotificacionesPush()  != null) builder.notificacionesPush(cmd.getNotificacionesPush());
        if (cmd.getNotificacionesEmail() != null) builder.notificacionesEmail(cmd.getNotificacionesEmail());
        if (cmd.getNotificacionesVisuales()!= null) builder.notificacionesVisuales(cmd.getNotificacionesVisuales());
        if (cmd.getBadgesDinamicos()     != null) builder.badgesDinamicos(cmd.getBadgesDinamicos());
        if (cmd.getIdioma()              != null) builder.idioma(cmd.getIdioma());
        if (cmd.getZonaHoraria()         != null) builder.zonaHoraria(cmd.getZonaHoraria());
        if (cmd.getFormatoFecha()        != null) builder.formatoFecha(cmd.getFormatoFecha());
        if (cmd.getFormatoHora()         != null) builder.formatoHora(cmd.getFormatoHora());
        if (cmd.getPrimerDiaSemana()     != null) builder.primerDiaSemana(cmd.getPrimerDiaSemana());
        if (cmd.getAltoContraste()       != null) builder.altoContraste(cmd.getAltoContraste());
        if (cmd.getReducirAnimaciones()  != null) builder.reducirAnimaciones(cmd.getReducirAnimaciones());
        if (cmd.getAumentarEspaciado()   != null) builder.aumentarEspaciado(cmd.getAumentarEspaciado());
        if (cmd.getCursorGrande()        != null) builder.cursorGrande(cmd.getCursorGrande());

        return repository.save(builder.build());
    }

    @Override
    @Transactional
    public PreferenciaAdmin reset(Long idUsuarioAdmin) {
        PreferenciaAdmin existing = getOrCreate(idUsuarioAdmin);
        PreferenciaAdmin defaults = PreferenciaAdmin.defaultsFor(idUsuarioAdmin)
                .toBuilder()
                .id(existing.getId())
                .build();
        return repository.save(defaults);
    }
}
