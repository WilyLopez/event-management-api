package com.playzone.pems.infrastructure.persistence.preferencia.entity;

import com.playzone.pems.domain.preferencia.model.enums.*;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "preferenciaadmin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaAdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpreferenciaadmin")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuarioadmin", nullable = false, unique = true)
    private UsuarioAdminEntity usuarioAdmin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TemaAdmin tema = TemaAdmin.SYSTEM;

    @Column(name = "colorprimario", length = 20)
    private String colorPrimario;

    @Column(name = "colorsecundario", length = 20)
    private String colorSecundario;

    @Column(name = "colorsidebar", length = 20)
    private String colorSidebar;

    @Column(name = "coloracento", length = 20)
    private String colorAcento;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String tipografia = "INTER";

    @Enumerated(EnumType.STRING)
    @Column(name = "tamanofuente", nullable = false, length = 20)
    @Builder.Default
    private TamanioFuente tamanioFuente = TamanioFuente.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "radiosbordes", nullable = false, length = 20)
    @Builder.Default
    private RadiosBordes radiosBordes = RadiosBordes.NORMAL;

    @Column(name = "sidebarcolapsado", nullable = false)
    @Builder.Default
    private boolean sidebarColapsado = false;

    @Column(name = "sidebarflotante", nullable = false)
    @Builder.Default
    private boolean sidebarFlotante = false;

    @Column(name = "modocompacto", nullable = false)
    @Builder.Default
    private boolean modoCompacto = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "anchocontenido", nullable = false, length = 20)
    @Builder.Default
    private AnchoContenido anchoContenido = AnchoContenido.FULL;

    @Column(name = "mostrarmigaspan", nullable = false)
    @Builder.Default
    private boolean mostrarMigaspan = true;

    @Column(name = "mostrariconosmenu", nullable = false)
    @Builder.Default
    private boolean mostrarIconosMenu = true;

    @Column(name = "mostraranimaciones", nullable = false)
    @Builder.Default
    private boolean mostrarAnimaciones = true;

    @Column(name = "animacionsidebar", nullable = false)
    @Builder.Default
    private boolean animacionSidebar = true;

    @Column(name = "hovereffects", nullable = false)
    @Builder.Default
    private boolean hoverEffects = true;

    @Column(name = "loadersanimados", nullable = false)
    @Builder.Default
    private boolean loadersAnimados = true;

    @Column(name = "confirmaracciones", nullable = false)
    @Builder.Default
    private boolean confirmarAcciones = true;

    @Column(name = "recordarultimapagina", nullable = false)
    @Builder.Default
    private boolean recordarUltimaPagina = true;

    @Column(name = "restaurartabs", nullable = false)
    @Builder.Default
    private boolean restaurarTabs = false;

    @Column(name = "autorefreshdashboard", nullable = false)
    @Builder.Default
    private boolean autoRefreshDashboard = false;

    @Column(name = "intervalorefreshseg")
    @Builder.Default
    private Integer intervaloRefreshSeg = 60;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dashboardpersonalizado", columnDefinition = "jsonb")
    private String dashboardPersonalizado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "widgetsvisibles", columnDefinition = "jsonb")
    private String widgetsVisibles;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "accesosrapidos", columnDefinition = "jsonb")
    private String accesosRapidos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ordenwidgets", columnDefinition = "jsonb")
    private String ordenWidgets;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "layoutdashboard", columnDefinition = "jsonb")
    private String layoutDashboard;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "filtrospersistentes", columnDefinition = "jsonb")
    private String filtrosPersistentes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "columnasvisibles", columnDefinition = "jsonb")
    private String columnasVisibles;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ordenamientotablas", columnDefinition = "jsonb")
    private String ordenamientoTablas;

    @Column(name = "elementosportabla", nullable = false)
    @Builder.Default
    private int elementosPorTabla = 10;

    @Column(name = "sonidonotificaciones", nullable = false)
    @Builder.Default
    private boolean sonidoNotificaciones = true;

    @Column(name = "notificacionespush", nullable = false)
    @Builder.Default
    private boolean notificacionesPush = true;

    @Column(name = "notificacionesemail", nullable = false)
    @Builder.Default
    private boolean notificacionesEmail = true;

    @Column(name = "notificacionesvisuales", nullable = false)
    @Builder.Default
    private boolean notificacionesVisuales = true;

    @Column(name = "badgesdinamicos", nullable = false)
    @Builder.Default
    private boolean badgesDinamicos = true;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String idioma = "es";

    @Column(name = "zonahoraria", nullable = false, length = 60)
    @Builder.Default
    private String zonaHoraria = "America/Lima";

    @Column(name = "formatofecha", nullable = false, length = 30)
    @Builder.Default
    private String formatoFecha = "DD/MM/YYYY";

    @Column(name = "formatohora", nullable = false, length = 20)
    @Builder.Default
    private String formatoHora = "24H";

    @Enumerated(EnumType.STRING)
    @Column(name = "primerdiasemana", nullable = false, length = 20)
    @Builder.Default
    private PrimerDiaSemana primerDiaSemana = PrimerDiaSemana.MONDAY;

    @Column(name = "altocontraste", nullable = false)
    @Builder.Default
    private boolean altoContraste = false;

    @Column(name = "reduciranimaciones", nullable = false)
    @Builder.Default
    private boolean reducirAnimaciones = false;

    @Column(name = "aumentarespaciado", nullable = false)
    @Builder.Default
    private boolean aumentarEspaciado = false;

    @Column(name = "cursorgrande", nullable = false)
    @Builder.Default
    private boolean cursorGrande = false;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
