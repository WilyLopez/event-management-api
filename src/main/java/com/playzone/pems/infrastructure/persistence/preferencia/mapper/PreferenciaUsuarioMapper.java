package com.playzone.pems.infrastructure.persistence.preferencia.mapper;

import com.playzone.pems.domain.preferencia.model.PreferenciaUsuario;
import com.playzone.pems.infrastructure.persistence.preferencia.entity.PreferenciaUsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PreferenciaUsuarioMapper {

    public PreferenciaUsuario toDomain(PreferenciaUsuarioEntity e) {
        Map<String, Object> x = e.getPreferenciasExtras() != null ? e.getPreferenciasExtras() : Map.of();
        return PreferenciaUsuario.builder()
                .usuarioId(e.getUsuarioId())
                .tema(e.getTema())
                .idioma(e.getIdioma())
                .zonaHoraria(e.getZonaHoraria())
                .formatoFecha(e.getFormatoFecha())
                .formatoHora(e.getFormatoHora())
                .sidebarColapsado(e.isSidebarColapsado())
                .autoRefreshDashboard(e.isAutorefreshDashboard())
                .colorPrimario(str(x, "colorPrimario", null))
                .colorSecundario(str(x, "colorSecundario", null))
                .colorSidebar(str(x, "colorSidebar", null))
                .colorAcento(str(x, "colorAcento", null))
                .tipografia(str(x, "tipografia", "Inter"))
                .tamanioFuente(str(x, "tamanioFuente", "NORMAL"))
                .radiosBordes(str(x, "radiosBordes", "NORMAL"))
                .sidebarFlotante(bool(x, "sidebarFlotante", false))
                .modoCompacto(bool(x, "modoCompacto", false))
                .anchoContenido(str(x, "anchoContenido", "BOXED"))
                .mostrarMigaspan(bool(x, "mostrarMigaspan", true))
                .mostrarIconosMenu(bool(x, "mostrarIconosMenu", true))
                .mostrarAnimaciones(bool(x, "mostrarAnimaciones", true))
                .animacionSidebar(bool(x, "animacionSidebar", true))
                .hoverEffects(bool(x, "hoverEffects", true))
                .loadersAnimados(bool(x, "loadersAnimados", true))
                .confirmarAcciones(bool(x, "confirmarAcciones", true))
                .recordarUltimaPagina(bool(x, "recordarUltimaPagina", true))
                .restaurarTabs(bool(x, "restaurarTabs", false))
                .intervaloRefreshSeg(num(x, "intervaloRefreshSeg", 30))
                .dashboardPersonalizado(x.get("dashboardPersonalizado"))
                .widgetsVisibles(x.get("widgetsVisibles"))
                .accesosRapidos(x.get("accesosRapidos"))
                .ordenWidgets(x.get("ordenWidgets"))
                .layoutDashboard(x.get("layoutDashboard"))
                .filtrosPersistentes(x.get("filtrosPersistentes"))
                .columnasVisibles(x.get("columnasVisibles"))
                .ordenamientoTablas(x.get("ordenamientoTablas"))
                .elementosPorTabla(num(x, "elementosPorTabla", 10))
                .sonidoNotificaciones(bool(x, "sonidoNotificaciones", false))
                .notificacionesPush(bool(x, "notificacionesPush", true))
                .notificacionesEmail(bool(x, "notificacionesEmail", true))
                .notificacionesVisuales(bool(x, "notificacionesVisuales", true))
                .badgesDinamicos(bool(x, "badgesDinamicos", true))
                .primerDiaSemana(str(x, "primerDiaSemana", "MONDAY"))
                .altoContraste(bool(x, "altoContraste", false))
                .reducirAnimaciones(bool(x, "reducirAnimaciones", false))
                .aumentarEspaciado(bool(x, "aumentarEspaciado", false))
                .cursorGrande(bool(x, "cursorGrande", false))
                .fechaCreacion(e.getCreatedAt())
                .fechaActualizacion(e.getUpdatedAt())
                .build();
    }

    public PreferenciaUsuarioEntity toEntity(PreferenciaUsuario d) {
        Map<String, Object> extras = buildExtras(d);
        return PreferenciaUsuarioEntity.builder()
                .usuarioId(d.getUsuarioId())
                .tema(d.getTema() != null ? d.getTema() : "SYSTEM")
                .idioma(d.getIdioma() != null ? d.getIdioma() : "es")
                .zonaHoraria(d.getZonaHoraria() != null ? d.getZonaHoraria() : "America/Lima")
                .formatoFecha(d.getFormatoFecha() != null ? d.getFormatoFecha() : "DD/MM/YYYY")
                .formatoHora(d.getFormatoHora() != null ? d.getFormatoHora() : "24H")
                .sidebarColapsado(d.isSidebarColapsado())
                .autorefreshDashboard(d.isAutoRefreshDashboard())
                .preferenciasExtras(extras)
                .build();
    }

    private Map<String, Object> buildExtras(PreferenciaUsuario d) {
        Map<String, Object> m = new HashMap<>();
        m.put("colorPrimario",          d.getColorPrimario());
        m.put("colorSecundario",         d.getColorSecundario());
        m.put("colorSidebar",            d.getColorSidebar());
        m.put("colorAcento",             d.getColorAcento());
        m.put("tipografia",              d.getTipografia() != null ? d.getTipografia() : "Inter");
        m.put("tamanioFuente",           d.getTamanioFuente() != null ? d.getTamanioFuente() : "NORMAL");
        m.put("radiosBordes",            d.getRadiosBordes() != null ? d.getRadiosBordes() : "NORMAL");
        m.put("sidebarFlotante",         d.isSidebarFlotante());
        m.put("modoCompacto",            d.isModoCompacto());
        m.put("anchoContenido",          d.getAnchoContenido() != null ? d.getAnchoContenido() : "BOXED");
        m.put("mostrarMigaspan",         d.isMostrarMigaspan());
        m.put("mostrarIconosMenu",       d.isMostrarIconosMenu());
        m.put("mostrarAnimaciones",      d.isMostrarAnimaciones());
        m.put("animacionSidebar",        d.isAnimacionSidebar());
        m.put("hoverEffects",            d.isHoverEffects());
        m.put("loadersAnimados",         d.isLoadersAnimados());
        m.put("confirmarAcciones",       d.isConfirmarAcciones());
        m.put("recordarUltimaPagina",    d.isRecordarUltimaPagina());
        m.put("restaurarTabs",           d.isRestaurarTabs());
        m.put("intervaloRefreshSeg",     d.getIntervaloRefreshSeg());
        m.put("dashboardPersonalizado",  d.getDashboardPersonalizado());
        m.put("widgetsVisibles",         d.getWidgetsVisibles());
        m.put("accesosRapidos",          d.getAccesosRapidos());
        m.put("ordenWidgets",            d.getOrdenWidgets());
        m.put("layoutDashboard",         d.getLayoutDashboard());
        m.put("filtrosPersistentes",     d.getFiltrosPersistentes());
        m.put("columnasVisibles",        d.getColumnasVisibles());
        m.put("ordenamientoTablas",      d.getOrdenamientoTablas());
        m.put("elementosPorTabla",       d.getElementosPorTabla());
        m.put("sonidoNotificaciones",    d.isSonidoNotificaciones());
        m.put("notificacionesPush",      d.isNotificacionesPush());
        m.put("notificacionesEmail",     d.isNotificacionesEmail());
        m.put("notificacionesVisuales",  d.isNotificacionesVisuales());
        m.put("badgesDinamicos",         d.isBadgesDinamicos());
        m.put("primerDiaSemana",         d.getPrimerDiaSemana() != null ? d.getPrimerDiaSemana() : "MONDAY");
        m.put("altoContraste",           d.isAltoContraste());
        m.put("reducirAnimaciones",      d.isReducirAnimaciones());
        m.put("aumentarEspaciado",       d.isAumentarEspaciado());
        m.put("cursorGrande",            d.isCursorGrande());
        return m;
    }

    private static boolean bool(Map<String, Object> m, String k, boolean def) {
        Object v = m.get(k);
        return v instanceof Boolean b ? b : def;
    }

    private static String str(Map<String, Object> m, String k, String def) {
        Object v = m.get(k);
        return v instanceof String s ? s : def;
    }

    private static int num(Map<String, Object> m, String k, int def) {
        Object v = m.get(k);
        return v instanceof Number n ? n.intValue() : def;
    }
}
