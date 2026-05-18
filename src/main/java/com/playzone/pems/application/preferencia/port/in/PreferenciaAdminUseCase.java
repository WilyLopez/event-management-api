package com.playzone.pems.application.preferencia.port.in;

import com.playzone.pems.domain.preferencia.model.PreferenciaAdmin;
import com.playzone.pems.domain.preferencia.model.enums.*;
import lombok.Builder;
import lombok.Getter;

public interface PreferenciaAdminUseCase {

    PreferenciaAdmin getOrCreate(Long idUsuarioAdmin);

    PreferenciaAdmin update(Long idUsuarioAdmin, UpdateCommand command);

    PreferenciaAdmin patch(Long idUsuarioAdmin, PatchCommand command);

    PreferenciaAdmin reset(Long idUsuarioAdmin);

    @Getter
    @Builder
    class UpdateCommand {
        private final TemaAdmin       tema;
        private final String          colorPrimario;
        private final String          colorSecundario;
        private final String          colorSidebar;
        private final String          colorAcento;
        private final String          tipografia;
        private final TamanioFuente   tamanioFuente;
        private final RadiosBordes    radiosBordes;
        private final boolean         sidebarColapsado;
        private final boolean         sidebarFlotante;
        private final boolean         modoCompacto;
        private final AnchoContenido  anchoContenido;
        private final boolean         mostrarMigaspan;
        private final boolean         mostrarIconosMenu;
        private final boolean         mostrarAnimaciones;
        private final boolean         animacionSidebar;
        private final boolean         hoverEffects;
        private final boolean         loadersAnimados;
        private final boolean         confirmarAcciones;
        private final boolean         recordarUltimaPagina;
        private final boolean         restaurarTabs;
        private final boolean         autoRefreshDashboard;
        private final Integer         intervaloRefreshSeg;
        private final String          dashboardPersonalizado;
        private final String          widgetsVisibles;
        private final String          accesosRapidos;
        private final String          ordenWidgets;
        private final String          layoutDashboard;
        private final String          filtrosPersistentes;
        private final String          columnasVisibles;
        private final String          ordenamientoTablas;
        private final int             elementosPorTabla;
        private final boolean         sonidoNotificaciones;
        private final boolean         notificacionesPush;
        private final boolean         notificacionesEmail;
        private final boolean         notificacionesVisuales;
        private final boolean         badgesDinamicos;
        private final String          idioma;
        private final String          zonaHoraria;
        private final String          formatoFecha;
        private final String          formatoHora;
        private final PrimerDiaSemana primerDiaSemana;
        private final boolean         altoContraste;
        private final boolean         reducirAnimaciones;
        private final boolean         aumentarEspaciado;
        private final boolean         cursorGrande;
    }

    @Getter
    @Builder
    class PatchCommand {
        private final TemaAdmin       tema;
        private final String          colorPrimario;
        private final String          colorSecundario;
        private final String          colorSidebar;
        private final String          colorAcento;
        private final String          tipografia;
        private final TamanioFuente   tamanioFuente;
        private final RadiosBordes    radiosBordes;
        private final Boolean         sidebarColapsado;
        private final Boolean         sidebarFlotante;
        private final Boolean         modoCompacto;
        private final AnchoContenido  anchoContenido;
        private final Boolean         mostrarMigaspan;
        private final Boolean         mostrarIconosMenu;
        private final Boolean         mostrarAnimaciones;
        private final Boolean         animacionSidebar;
        private final Boolean         hoverEffects;
        private final Boolean         loadersAnimados;
        private final Boolean         confirmarAcciones;
        private final Boolean         recordarUltimaPagina;
        private final Boolean         restaurarTabs;
        private final Boolean         autoRefreshDashboard;
        private final Integer         intervaloRefreshSeg;
        private final String          dashboardPersonalizado;
        private final String          widgetsVisibles;
        private final String          accesosRapidos;
        private final String          ordenWidgets;
        private final String          layoutDashboard;
        private final String          filtrosPersistentes;
        private final String          columnasVisibles;
        private final String          ordenamientoTablas;
        private final Integer         elementosPorTabla;
        private final Boolean         sonidoNotificaciones;
        private final Boolean         notificacionesPush;
        private final Boolean         notificacionesEmail;
        private final Boolean         notificacionesVisuales;
        private final Boolean         badgesDinamicos;
        private final String          idioma;
        private final String          zonaHoraria;
        private final String          formatoFecha;
        private final String          formatoHora;
        private final PrimerDiaSemana primerDiaSemana;
        private final Boolean         altoContraste;
        private final Boolean         reducirAnimaciones;
        private final Boolean         aumentarEspaciado;
        private final Boolean         cursorGrande;
    }
}
