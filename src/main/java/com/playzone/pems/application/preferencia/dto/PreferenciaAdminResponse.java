package com.playzone.pems.application.preferencia.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.playzone.pems.domain.preferencia.model.PreferenciaAdmin;
import com.playzone.pems.domain.preferencia.model.enums.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaAdminResponse {

    private Long            id;
    private Long            idUsuarioAdmin;

    private TemaAdmin       tema;
    private String          colorPrimario;
    private String          colorSecundario;
    private String          colorSidebar;
    private String          colorAcento;

    private String          tipografia;
    private TamanioFuente   tamanioFuente;
    private RadiosBordes    radiosBordes;

    private boolean         sidebarColapsado;
    private boolean         sidebarFlotante;
    private boolean         modoCompacto;
    private AnchoContenido  anchoContenido;
    private boolean         mostrarMigaspan;
    private boolean         mostrarIconosMenu;

    private boolean         mostrarAnimaciones;
    private boolean         animacionSidebar;
    private boolean         hoverEffects;
    private boolean         loadersAnimados;

    private boolean         confirmarAcciones;
    private boolean         recordarUltimaPagina;
    private boolean         restaurarTabs;
    private boolean         autoRefreshDashboard;
    private Integer         intervaloRefreshSeg;

    @JsonRawValue private String dashboardPersonalizado;
    @JsonRawValue private String widgetsVisibles;
    @JsonRawValue private String accesosRapidos;
    @JsonRawValue private String ordenWidgets;
    @JsonRawValue private String layoutDashboard;
    @JsonRawValue private String filtrosPersistentes;
    @JsonRawValue private String columnasVisibles;
    @JsonRawValue private String ordenamientoTablas;

    private int             elementosPorTabla;

    private boolean         sonidoNotificaciones;
    private boolean         notificacionesPush;
    private boolean         notificacionesEmail;
    private boolean         notificacionesVisuales;
    private boolean         badgesDinamicos;

    private String          idioma;
    private String          zonaHoraria;
    private String          formatoFecha;
    private String          formatoHora;
    private PrimerDiaSemana primerDiaSemana;

    private boolean         altoContraste;
    private boolean         reducirAnimaciones;
    private boolean         aumentarEspaciado;
    private boolean         cursorGrande;

    private LocalDateTime   fechaCreacion;
    private LocalDateTime   fechaActualizacion;

    public static PreferenciaAdminResponse from(PreferenciaAdmin domain) {
        return PreferenciaAdminResponse.builder()
                .id(domain.getId())
                .idUsuarioAdmin(domain.getIdUsuarioAdmin())
                .tema(domain.getTema())
                .colorPrimario(domain.getColorPrimario())
                .colorSecundario(domain.getColorSecundario())
                .colorSidebar(domain.getColorSidebar())
                .colorAcento(domain.getColorAcento())
                .tipografia(domain.getTipografia())
                .tamanioFuente(domain.getTamanioFuente())
                .radiosBordes(domain.getRadiosBordes())
                .sidebarColapsado(domain.isSidebarColapsado())
                .sidebarFlotante(domain.isSidebarFlotante())
                .modoCompacto(domain.isModoCompacto())
                .anchoContenido(domain.getAnchoContenido())
                .mostrarMigaspan(domain.isMostrarMigaspan())
                .mostrarIconosMenu(domain.isMostrarIconosMenu())
                .mostrarAnimaciones(domain.isMostrarAnimaciones())
                .animacionSidebar(domain.isAnimacionSidebar())
                .hoverEffects(domain.isHoverEffects())
                .loadersAnimados(domain.isLoadersAnimados())
                .confirmarAcciones(domain.isConfirmarAcciones())
                .recordarUltimaPagina(domain.isRecordarUltimaPagina())
                .restaurarTabs(domain.isRestaurarTabs())
                .autoRefreshDashboard(domain.isAutoRefreshDashboard())
                .intervaloRefreshSeg(domain.getIntervaloRefreshSeg())
                .dashboardPersonalizado(domain.getDashboardPersonalizado())
                .widgetsVisibles(domain.getWidgetsVisibles())
                .accesosRapidos(domain.getAccesosRapidos())
                .ordenWidgets(domain.getOrdenWidgets())
                .layoutDashboard(domain.getLayoutDashboard())
                .filtrosPersistentes(domain.getFiltrosPersistentes())
                .columnasVisibles(domain.getColumnasVisibles())
                .ordenamientoTablas(domain.getOrdenamientoTablas())
                .elementosPorTabla(domain.getElementosPorTabla())
                .sonidoNotificaciones(domain.isSonidoNotificaciones())
                .notificacionesPush(domain.isNotificacionesPush())
                .notificacionesEmail(domain.isNotificacionesEmail())
                .notificacionesVisuales(domain.isNotificacionesVisuales())
                .badgesDinamicos(domain.isBadgesDinamicos())
                .idioma(domain.getIdioma())
                .zonaHoraria(domain.getZonaHoraria())
                .formatoFecha(domain.getFormatoFecha())
                .formatoHora(domain.getFormatoHora())
                .primerDiaSemana(domain.getPrimerDiaSemana())
                .altoContraste(domain.isAltoContraste())
                .reducirAnimaciones(domain.isReducirAnimaciones())
                .aumentarEspaciado(domain.isAumentarEspaciado())
                .cursorGrande(domain.isCursorGrande())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}
