package com.playzone.pems.application.preferencia.service;

import com.playzone.pems.application.preferencia.dto.command.ActualizarPreferenciaAdminCommand;
import com.playzone.pems.application.preferencia.dto.response.PreferenciaAdminResponse;
import com.playzone.pems.application.preferencia.port.in.ActualizarPreferenciaAdminUseCase;
import com.playzone.pems.application.preferencia.port.in.ObtenerPreferenciaAdminUseCase;
import com.playzone.pems.domain.preferencia.model.PreferenciaUsuario;
import com.playzone.pems.domain.preferencia.repository.PreferenciaUsuarioRepository;
import com.playzone.pems.shared.exception.ForbiddenException;
import com.playzone.pems.infrastructure.security.SupabaseAuthContext;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PreferenciaAdminService
        implements ObtenerPreferenciaAdminUseCase, ActualizarPreferenciaAdminUseCase {

    private static final List<String> ROLES_PERMITIDOS = List.of("ADMIN", "SUPERADMIN");

    private final PreferenciaUsuarioRepository repository;
    private final SupabaseAuthFacade           authFacade;

    @Override
    @Transactional(readOnly = true)
    public PreferenciaAdminResponse obtener(UUID usuarioId) {
        validarRol();
        PreferenciaUsuario pref = repository.buscarPorUsuarioId(usuarioId)
                .orElseGet(() -> defaults(usuarioId));
        return toResponse(pref);
    }

    @Override
    @Transactional
    public PreferenciaAdminResponse actualizar(UUID usuarioId, ActualizarPreferenciaAdminCommand cmd) {
        validarRol();
        PreferenciaUsuario pref = fromCommand(usuarioId, cmd);
        return toResponse(repository.guardar(pref));
    }

    @Override
    @Transactional
    public PreferenciaAdminResponse parchear(UUID usuarioId, Map<String, Object> patch) {
        validarRol();
        PreferenciaUsuario existing = repository.buscarPorUsuarioId(usuarioId)
                .orElseGet(() -> defaults(usuarioId));
        PreferenciaUsuario patched = applyPatch(existing, patch);
        return toResponse(repository.guardar(patched));
    }

    @Override
    @Transactional
    public PreferenciaAdminResponse resetear(UUID usuarioId) {
        validarRol();
        return toResponse(repository.guardar(defaults(usuarioId)));
    }

    private void validarRol() {
        SupabaseAuthContext ctx = authFacade.contextoActual()
                .orElseThrow(() -> new UnauthorizedException("No autenticado"));
        boolean permitido = ctx.roles().stream().anyMatch(ROLES_PERMITIDOS::contains);
        if (!permitido) {
            throw new ForbiddenException("Solo ADMIN y SUPERADMIN pueden gestionar preferencias administrativas.");
        }
    }

    private PreferenciaUsuario defaults(UUID usuarioId) {
        return PreferenciaUsuario.builder()
                .usuarioId(usuarioId)
                .tema("SYSTEM")
                .idioma("es")
                .zonaHoraria("America/Lima")
                .formatoFecha("DD/MM/YYYY")
                .formatoHora("24H")
                .sidebarColapsado(false)
                .autoRefreshDashboard(false)
                .colorPrimario(null)
                .colorSecundario(null)
                .colorSidebar(null)
                .colorAcento(null)
                .tipografia("Inter")
                .tamanioFuente("NORMAL")
                .radiosBordes("NORMAL")
                .sidebarFlotante(false)
                .modoCompacto(false)
                .anchoContenido("BOXED")
                .mostrarMigaspan(true)
                .mostrarIconosMenu(true)
                .mostrarAnimaciones(true)
                .animacionSidebar(true)
                .hoverEffects(true)
                .loadersAnimados(true)
                .confirmarAcciones(true)
                .recordarUltimaPagina(true)
                .restaurarTabs(false)
                .intervaloRefreshSeg(30)
                .elementosPorTabla(10)
                .sonidoNotificaciones(false)
                .notificacionesPush(true)
                .notificacionesEmail(true)
                .notificacionesVisuales(true)
                .badgesDinamicos(true)
                .primerDiaSemana("MONDAY")
                .altoContraste(false)
                .reducirAnimaciones(false)
                .aumentarEspaciado(false)
                .cursorGrande(false)
                .build();
    }

    private PreferenciaUsuario fromCommand(UUID usuarioId, ActualizarPreferenciaAdminCommand c) {
        return PreferenciaUsuario.builder()
                .usuarioId(usuarioId)
                .tema(c.getTema())
                .idioma(c.getIdioma())
                .zonaHoraria(c.getZonaHoraria())
                .formatoFecha(c.getFormatoFecha())
                .formatoHora(c.getFormatoHora())
                .sidebarColapsado(c.isSidebarColapsado())
                .autoRefreshDashboard(c.isAutoRefreshDashboard())
                .colorPrimario(c.getColorPrimario())
                .colorSecundario(c.getColorSecundario())
                .colorSidebar(c.getColorSidebar())
                .colorAcento(c.getColorAcento())
                .tipografia(c.getTipografia())
                .tamanioFuente(c.getTamanioFuente())
                .radiosBordes(c.getRadiosBordes())
                .sidebarFlotante(c.isSidebarFlotante())
                .modoCompacto(c.isModoCompacto())
                .anchoContenido(c.getAnchoContenido())
                .mostrarMigaspan(c.isMostrarMigaspan())
                .mostrarIconosMenu(c.isMostrarIconosMenu())
                .mostrarAnimaciones(c.isMostrarAnimaciones())
                .animacionSidebar(c.isAnimacionSidebar())
                .hoverEffects(c.isHoverEffects())
                .loadersAnimados(c.isLoadersAnimados())
                .confirmarAcciones(c.isConfirmarAcciones())
                .recordarUltimaPagina(c.isRecordarUltimaPagina())
                .restaurarTabs(c.isRestaurarTabs())
                .intervaloRefreshSeg(c.getIntervaloRefreshSeg())
                .dashboardPersonalizado(c.getDashboardPersonalizado())
                .widgetsVisibles(c.getWidgetsVisibles())
                .accesosRapidos(c.getAccesosRapidos())
                .ordenWidgets(c.getOrdenWidgets())
                .layoutDashboard(c.getLayoutDashboard())
                .filtrosPersistentes(c.getFiltrosPersistentes())
                .columnasVisibles(c.getColumnasVisibles())
                .ordenamientoTablas(c.getOrdenamientoTablas())
                .elementosPorTabla(c.getElementosPorTabla())
                .sonidoNotificaciones(c.isSonidoNotificaciones())
                .notificacionesPush(c.isNotificacionesPush())
                .notificacionesEmail(c.isNotificacionesEmail())
                .notificacionesVisuales(c.isNotificacionesVisuales())
                .badgesDinamicos(c.isBadgesDinamicos())
                .primerDiaSemana(c.getPrimerDiaSemana())
                .altoContraste(c.isAltoContraste())
                .reducirAnimaciones(c.isReducirAnimaciones())
                .aumentarEspaciado(c.isAumentarEspaciado())
                .cursorGrande(c.isCursorGrande())
                .build();
    }

    private PreferenciaUsuario applyPatch(PreferenciaUsuario base, Map<String, Object> patch) {
        PreferenciaUsuario.PreferenciaUsuarioBuilder b = base.toBuilder();
        patch.forEach((key, value) -> {
            switch (key) {
                case "tema"                -> b.tema(str(value));
                case "idioma"              -> b.idioma(str(value));
                case "zonaHoraria"         -> b.zonaHoraria(str(value));
                case "formatoFecha"        -> b.formatoFecha(str(value));
                case "formatoHora"         -> b.formatoHora(str(value));
                case "sidebarColapsado"    -> b.sidebarColapsado(bool(value));
                case "autoRefreshDashboard"-> b.autoRefreshDashboard(bool(value));
                case "colorPrimario"       -> b.colorPrimario(str(value));
                case "colorSecundario"     -> b.colorSecundario(str(value));
                case "colorSidebar"        -> b.colorSidebar(str(value));
                case "colorAcento"         -> b.colorAcento(str(value));
                case "tipografia"          -> b.tipografia(str(value));
                case "tamanioFuente"       -> b.tamanioFuente(str(value));
                case "radiosBordes"        -> b.radiosBordes(str(value));
                case "sidebarFlotante"     -> b.sidebarFlotante(bool(value));
                case "modoCompacto"        -> b.modoCompacto(bool(value));
                case "anchoContenido"      -> b.anchoContenido(str(value));
                case "mostrarMigaspan"     -> b.mostrarMigaspan(bool(value));
                case "mostrarIconosMenu"   -> b.mostrarIconosMenu(bool(value));
                case "mostrarAnimaciones"  -> b.mostrarAnimaciones(bool(value));
                case "animacionSidebar"    -> b.animacionSidebar(bool(value));
                case "hoverEffects"        -> b.hoverEffects(bool(value));
                case "loadersAnimados"     -> b.loadersAnimados(bool(value));
                case "confirmarAcciones"   -> b.confirmarAcciones(bool(value));
                case "recordarUltimaPagina"-> b.recordarUltimaPagina(bool(value));
                case "restaurarTabs"       -> b.restaurarTabs(bool(value));
                case "intervaloRefreshSeg" -> b.intervaloRefreshSeg(num(value));
                case "dashboardPersonalizado" -> b.dashboardPersonalizado(value);
                case "widgetsVisibles"     -> b.widgetsVisibles(value);
                case "accesosRapidos"      -> b.accesosRapidos(value);
                case "ordenWidgets"        -> b.ordenWidgets(value);
                case "layoutDashboard"     -> b.layoutDashboard(value);
                case "filtrosPersistentes" -> b.filtrosPersistentes(value);
                case "columnasVisibles"    -> b.columnasVisibles(value);
                case "ordenamientoTablas"  -> b.ordenamientoTablas(value);
                case "elementosPorTabla"   -> b.elementosPorTabla(num(value));
                case "sonidoNotificaciones"-> b.sonidoNotificaciones(bool(value));
                case "notificacionesPush"  -> b.notificacionesPush(bool(value));
                case "notificacionesEmail" -> b.notificacionesEmail(bool(value));
                case "notificacionesVisuales" -> b.notificacionesVisuales(bool(value));
                case "badgesDinamicos"     -> b.badgesDinamicos(bool(value));
                case "primerDiaSemana"     -> b.primerDiaSemana(str(value));
                case "altoContraste"       -> b.altoContraste(bool(value));
                case "reducirAnimaciones"  -> b.reducirAnimaciones(bool(value));
                case "aumentarEspaciado"   -> b.aumentarEspaciado(bool(value));
                case "cursorGrande"        -> b.cursorGrande(bool(value));
                default -> { }
            }
        });
        return b.build();
    }

    private PreferenciaAdminResponse toResponse(PreferenciaUsuario p) {
        return PreferenciaAdminResponse.builder()
                .id(p.getUsuarioId().toString())
                .idUsuarioAdmin(p.getUsuarioId().toString())
                .tema(p.getTema())
                .idioma(p.getIdioma())
                .zonaHoraria(p.getZonaHoraria())
                .formatoFecha(p.getFormatoFecha())
                .formatoHora(p.getFormatoHora())
                .sidebarColapsado(p.isSidebarColapsado())
                .autoRefreshDashboard(p.isAutoRefreshDashboard())
                .colorPrimario(p.getColorPrimario())
                .colorSecundario(p.getColorSecundario())
                .colorSidebar(p.getColorSidebar())
                .colorAcento(p.getColorAcento())
                .tipografia(p.getTipografia())
                .tamanioFuente(p.getTamanioFuente())
                .radiosBordes(p.getRadiosBordes())
                .sidebarFlotante(p.isSidebarFlotante())
                .modoCompacto(p.isModoCompacto())
                .anchoContenido(p.getAnchoContenido())
                .mostrarMigaspan(p.isMostrarMigaspan())
                .mostrarIconosMenu(p.isMostrarIconosMenu())
                .mostrarAnimaciones(p.isMostrarAnimaciones())
                .animacionSidebar(p.isAnimacionSidebar())
                .hoverEffects(p.isHoverEffects())
                .loadersAnimados(p.isLoadersAnimados())
                .confirmarAcciones(p.isConfirmarAcciones())
                .recordarUltimaPagina(p.isRecordarUltimaPagina())
                .restaurarTabs(p.isRestaurarTabs())
                .intervaloRefreshSeg(p.getIntervaloRefreshSeg())
                .dashboardPersonalizado(p.getDashboardPersonalizado())
                .widgetsVisibles(p.getWidgetsVisibles())
                .accesosRapidos(p.getAccesosRapidos())
                .ordenWidgets(p.getOrdenWidgets())
                .layoutDashboard(p.getLayoutDashboard())
                .filtrosPersistentes(p.getFiltrosPersistentes())
                .columnasVisibles(p.getColumnasVisibles())
                .ordenamientoTablas(p.getOrdenamientoTablas())
                .elementosPorTabla(p.getElementosPorTabla())
                .sonidoNotificaciones(p.isSonidoNotificaciones())
                .notificacionesPush(p.isNotificacionesPush())
                .notificacionesEmail(p.isNotificacionesEmail())
                .notificacionesVisuales(p.isNotificacionesVisuales())
                .badgesDinamicos(p.isBadgesDinamicos())
                .primerDiaSemana(p.getPrimerDiaSemana())
                .altoContraste(p.isAltoContraste())
                .reducirAnimaciones(p.isReducirAnimaciones())
                .aumentarEspaciado(p.isAumentarEspaciado())
                .cursorGrande(p.isCursorGrande())
                .fechaCreacion(p.getFechaCreacion() != null ? p.getFechaCreacion().toString() : null)
                .fechaActualizacion(p.getFechaActualizacion() != null ? p.getFechaActualizacion().toString() : null)
                .build();
    }

    private static boolean bool(Object v) {
        return v instanceof Boolean b ? b : false;
    }

    private static String str(Object v) {
        return v instanceof String s ? s : null;
    }

    private static int num(Object v) {
        return v instanceof Number n ? n.intValue() : 0;
    }
}
