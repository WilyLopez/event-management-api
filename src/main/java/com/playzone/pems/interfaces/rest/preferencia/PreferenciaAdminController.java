package com.playzone.pems.interfaces.rest.preferencia;

import com.playzone.pems.application.preferencia.dto.PreferenciaAdminResponse;
import com.playzone.pems.application.preferencia.port.in.PreferenciaAdminUseCase;
import com.playzone.pems.domain.preferencia.model.enums.*;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/preferencias")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PreferenciaAdminController {

    private final PreferenciaAdminUseCase useCase;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> getMias(
            @RequestAttribute Long idUsuarioAdmin) {

        var result = useCase.getOrCreate(idUsuarioAdmin);
        return ResponseEntity.ok(ApiResponse.ok(PreferenciaAdminResponse.from(result)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> actualizar(
            @RequestAttribute Long idUsuarioAdmin,
            @Valid @RequestBody UpdateRequest request) {

        var cmd = PreferenciaAdminUseCase.UpdateCommand.builder()
                .tema(request.getTema())
                .colorPrimario(request.getColorPrimario())
                .colorSecundario(request.getColorSecundario())
                .colorSidebar(request.getColorSidebar())
                .colorAcento(request.getColorAcento())
                .tipografia(request.getTipografia())
                .tamanioFuente(request.getTamanioFuente())
                .radiosBordes(request.getRadiosBordes())
                .sidebarColapsado(request.isSidebarColapsado())
                .sidebarFlotante(request.isSidebarFlotante())
                .modoCompacto(request.isModoCompacto())
                .anchoContenido(request.getAnchoContenido())
                .mostrarMigaspan(request.isMostrarMigaspan())
                .mostrarIconosMenu(request.isMostrarIconosMenu())
                .mostrarAnimaciones(request.isMostrarAnimaciones())
                .animacionSidebar(request.isAnimacionSidebar())
                .hoverEffects(request.isHoverEffects())
                .loadersAnimados(request.isLoadersAnimados())
                .confirmarAcciones(request.isConfirmarAcciones())
                .recordarUltimaPagina(request.isRecordarUltimaPagina())
                .restaurarTabs(request.isRestaurarTabs())
                .autoRefreshDashboard(request.isAutoRefreshDashboard())
                .intervaloRefreshSeg(request.getIntervaloRefreshSeg())
                .dashboardPersonalizado(request.getDashboardPersonalizado())
                .widgetsVisibles(request.getWidgetsVisibles())
                .accesosRapidos(request.getAccesosRapidos())
                .ordenWidgets(request.getOrdenWidgets())
                .layoutDashboard(request.getLayoutDashboard())
                .filtrosPersistentes(request.getFiltrosPersistentes())
                .columnasVisibles(request.getColumnasVisibles())
                .ordenamientoTablas(request.getOrdenamientoTablas())
                .elementosPorTabla(request.getElementosPorTabla())
                .sonidoNotificaciones(request.isSonidoNotificaciones())
                .notificacionesPush(request.isNotificacionesPush())
                .notificacionesEmail(request.isNotificacionesEmail())
                .notificacionesVisuales(request.isNotificacionesVisuales())
                .badgesDinamicos(request.isBadgesDinamicos())
                .idioma(request.getIdioma())
                .zonaHoraria(request.getZonaHoraria())
                .formatoFecha(request.getFormatoFecha())
                .formatoHora(request.getFormatoHora())
                .primerDiaSemana(request.getPrimerDiaSemana())
                .altoContraste(request.isAltoContraste())
                .reducirAnimaciones(request.isReducirAnimaciones())
                .aumentarEspaciado(request.isAumentarEspaciado())
                .cursorGrande(request.isCursorGrande())
                .build();

        var result = useCase.update(idUsuarioAdmin, cmd);
        return ResponseEntity.ok(ApiResponse.ok(PreferenciaAdminResponse.from(result)));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> parchear(
            @RequestAttribute Long idUsuarioAdmin,
            @RequestBody PatchRequest request) {

        var cmd = PreferenciaAdminUseCase.PatchCommand.builder()
                .tema(request.getTema())
                .colorPrimario(request.getColorPrimario())
                .colorSecundario(request.getColorSecundario())
                .colorSidebar(request.getColorSidebar())
                .colorAcento(request.getColorAcento())
                .tipografia(request.getTipografia())
                .tamanioFuente(request.getTamanioFuente())
                .radiosBordes(request.getRadiosBordes())
                .sidebarColapsado(request.getSidebarColapsado())
                .sidebarFlotante(request.getSidebarFlotante())
                .modoCompacto(request.getModoCompacto())
                .anchoContenido(request.getAnchoContenido())
                .mostrarMigaspan(request.getMostrarMigaspan())
                .mostrarIconosMenu(request.getMostrarIconosMenu())
                .mostrarAnimaciones(request.getMostrarAnimaciones())
                .animacionSidebar(request.getAnimacionSidebar())
                .hoverEffects(request.getHoverEffects())
                .loadersAnimados(request.getLoadersAnimados())
                .confirmarAcciones(request.getConfirmarAcciones())
                .recordarUltimaPagina(request.getRecordarUltimaPagina())
                .restaurarTabs(request.getRestaurarTabs())
                .autoRefreshDashboard(request.getAutoRefreshDashboard())
                .intervaloRefreshSeg(request.getIntervaloRefreshSeg())
                .dashboardPersonalizado(request.getDashboardPersonalizado())
                .widgetsVisibles(request.getWidgetsVisibles())
                .accesosRapidos(request.getAccesosRapidos())
                .ordenWidgets(request.getOrdenWidgets())
                .layoutDashboard(request.getLayoutDashboard())
                .filtrosPersistentes(request.getFiltrosPersistentes())
                .columnasVisibles(request.getColumnasVisibles())
                .ordenamientoTablas(request.getOrdenamientoTablas())
                .elementosPorTabla(request.getElementosPorTabla())
                .sonidoNotificaciones(request.getSonidoNotificaciones())
                .notificacionesPush(request.getNotificacionesPush())
                .notificacionesEmail(request.getNotificacionesEmail())
                .notificacionesVisuales(request.getNotificacionesVisuales())
                .badgesDinamicos(request.getBadgesDinamicos())
                .idioma(request.getIdioma())
                .zonaHoraria(request.getZonaHoraria())
                .formatoFecha(request.getFormatoFecha())
                .formatoHora(request.getFormatoHora())
                .primerDiaSemana(request.getPrimerDiaSemana())
                .altoContraste(request.getAltoContraste())
                .reducirAnimaciones(request.getReducirAnimaciones())
                .aumentarEspaciado(request.getAumentarEspaciado())
                .cursorGrande(request.getCursorGrande())
                .build();

        var result = useCase.patch(idUsuarioAdmin, cmd);
        return ResponseEntity.ok(ApiResponse.ok(PreferenciaAdminResponse.from(result)));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> reset(
            @RequestAttribute Long idUsuarioAdmin) {

        var result = useCase.reset(idUsuarioAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Preferencias restablecidas a valores por defecto.",
                PreferenciaAdminResponse.from(result)));
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotNull private TemaAdmin       tema;
        private String                   colorPrimario;
        private String                   colorSecundario;
        private String                   colorSidebar;
        private String                   colorAcento;
        @NotNull @Size(max = 50) private String tipografia;
        @NotNull private TamanioFuente   tamanioFuente;
        @NotNull private RadiosBordes    radiosBordes;
        private boolean                  sidebarColapsado;
        private boolean                  sidebarFlotante;
        private boolean                  modoCompacto;
        @NotNull private AnchoContenido  anchoContenido;
        private boolean                  mostrarMigaspan;
        private boolean                  mostrarIconosMenu;
        private boolean                  mostrarAnimaciones;
        private boolean                  animacionSidebar;
        private boolean                  hoverEffects;
        private boolean                  loadersAnimados;
        private boolean                  confirmarAcciones;
        private boolean                  recordarUltimaPagina;
        private boolean                  restaurarTabs;
        private boolean                  autoRefreshDashboard;
        @Min(10) private Integer         intervaloRefreshSeg;
        private String                   dashboardPersonalizado;
        private String                   widgetsVisibles;
        private String                   accesosRapidos;
        private String                   ordenWidgets;
        private String                   layoutDashboard;
        private String                   filtrosPersistentes;
        private String                   columnasVisibles;
        private String                   ordenamientoTablas;
        @Min(1) private int              elementosPorTabla;
        private boolean                  sonidoNotificaciones;
        private boolean                  notificacionesPush;
        private boolean                  notificacionesEmail;
        private boolean                  notificacionesVisuales;
        private boolean                  badgesDinamicos;
        @NotNull @Size(max = 10) private String idioma;
        @NotNull @Size(max = 60) private String zonaHoraria;
        @NotNull @Size(max = 30) private String formatoFecha;
        @NotNull @Size(max = 20) private String formatoHora;
        @NotNull private PrimerDiaSemana primerDiaSemana;
        private boolean                  altoContraste;
        private boolean                  reducirAnimaciones;
        private boolean                  aumentarEspaciado;
        private boolean                  cursorGrande;
    }

    @Getter
    @NoArgsConstructor
    public static class PatchRequest {
        private TemaAdmin       tema;
        private String          colorPrimario;
        private String          colorSecundario;
        private String          colorSidebar;
        private String          colorAcento;
        private String          tipografia;
        private TamanioFuente   tamanioFuente;
        private RadiosBordes    radiosBordes;
        private Boolean         sidebarColapsado;
        private Boolean         sidebarFlotante;
        private Boolean         modoCompacto;
        private AnchoContenido  anchoContenido;
        private Boolean         mostrarMigaspan;
        private Boolean         mostrarIconosMenu;
        private Boolean         mostrarAnimaciones;
        private Boolean         animacionSidebar;
        private Boolean         hoverEffects;
        private Boolean         loadersAnimados;
        private Boolean         confirmarAcciones;
        private Boolean         recordarUltimaPagina;
        private Boolean         restaurarTabs;
        private Boolean         autoRefreshDashboard;
        private Integer         intervaloRefreshSeg;
        private String          dashboardPersonalizado;
        private String          widgetsVisibles;
        private String          accesosRapidos;
        private String          ordenWidgets;
        private String          layoutDashboard;
        private String          filtrosPersistentes;
        private String          columnasVisibles;
        private String          ordenamientoTablas;
        private Integer         elementosPorTabla;
        private Boolean         sonidoNotificaciones;
        private Boolean         notificacionesPush;
        private Boolean         notificacionesEmail;
        private Boolean         notificacionesVisuales;
        private Boolean         badgesDinamicos;
        private String          idioma;
        private String          zonaHoraria;
        private String          formatoFecha;
        private String          formatoHora;
        private PrimerDiaSemana primerDiaSemana;
        private Boolean         altoContraste;
        private Boolean         reducirAnimaciones;
        private Boolean         aumentarEspaciado;
        private Boolean         cursorGrande;
    }
}
