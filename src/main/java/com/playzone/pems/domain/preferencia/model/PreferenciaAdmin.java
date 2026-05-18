package com.playzone.pems.domain.preferencia.model;

import com.playzone.pems.domain.preferencia.model.enums.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaAdmin {

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

    private String          dashboardPersonalizado;
    private String          widgetsVisibles;
    private String          accesosRapidos;
    private String          ordenWidgets;
    private String          layoutDashboard;
    private String          filtrosPersistentes;
    private String          columnasVisibles;
    private String          ordenamientoTablas;
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

    public static PreferenciaAdmin defaultsFor(Long idUsuarioAdmin) {
        return PreferenciaAdmin.builder()
                .idUsuarioAdmin(idUsuarioAdmin)
                .tema(TemaAdmin.SYSTEM)
                .tipografia("INTER")
                .tamanioFuente(TamanioFuente.NORMAL)
                .radiosBordes(RadiosBordes.NORMAL)
                .sidebarColapsado(false)
                .sidebarFlotante(false)
                .modoCompacto(false)
                .anchoContenido(AnchoContenido.FULL)
                .mostrarMigaspan(true)
                .mostrarIconosMenu(true)
                .mostrarAnimaciones(true)
                .animacionSidebar(true)
                .hoverEffects(true)
                .loadersAnimados(true)
                .confirmarAcciones(true)
                .recordarUltimaPagina(true)
                .restaurarTabs(false)
                .autoRefreshDashboard(false)
                .intervaloRefreshSeg(60)
                .elementosPorTabla(10)
                .sonidoNotificaciones(true)
                .notificacionesPush(true)
                .notificacionesEmail(true)
                .notificacionesVisuales(true)
                .badgesDinamicos(true)
                .idioma("es")
                .zonaHoraria("America/Lima")
                .formatoFecha("DD/MM/YYYY")
                .formatoHora("24H")
                .primerDiaSemana(PrimerDiaSemana.MONDAY)
                .altoContraste(false)
                .reducirAnimaciones(false)
                .aumentarEspaciado(false)
                .cursorGrande(false)
                .build();
    }
}
