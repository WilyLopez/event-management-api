package com.playzone.pems.interfaces.rest.preferencia.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarPreferenciaAdminRequest {

    private String  tema;
    private String  idioma;
    private String  zonaHoraria;
    private String  formatoFecha;
    private String  formatoHora;
    private boolean sidebarColapsado;
    private boolean autoRefreshDashboard;

    private String  colorPrimario;
    private String  colorSecundario;
    private String  colorSidebar;
    private String  colorAcento;
    private String  tipografia;
    private String  tamanioFuente;
    private String  radiosBordes;

    private boolean sidebarFlotante;
    private boolean modoCompacto;
    private String  anchoContenido;
    private boolean mostrarMigaspan;
    private boolean mostrarIconosMenu;

    private boolean mostrarAnimaciones;
    private boolean animacionSidebar;
    private boolean hoverEffects;
    private boolean loadersAnimados;

    private boolean confirmarAcciones;
    private boolean recordarUltimaPagina;
    private boolean restaurarTabs;
    private int     intervaloRefreshSeg;

    private Object  dashboardPersonalizado;
    private Object  widgetsVisibles;
    private Object  accesosRapidos;
    private Object  ordenWidgets;
    private Object  layoutDashboard;

    private Object  filtrosPersistentes;
    private Object  columnasVisibles;
    private Object  ordenamientoTablas;
    private int     elementosPorTabla;

    private boolean sonidoNotificaciones;
    private boolean notificacionesPush;
    private boolean notificacionesEmail;
    private boolean notificacionesVisuales;
    private boolean badgesDinamicos;

    private String  primerDiaSemana;

    private boolean altoContraste;
    private boolean reducirAnimaciones;
    private boolean aumentarEspaciado;
    private boolean cursorGrande;
}
