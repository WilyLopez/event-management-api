package com.playzone.pems.interfaces.rest.preferencia;

import com.playzone.pems.application.preferencia.dto.command.ActualizarPreferenciaAdminCommand;
import com.playzone.pems.application.preferencia.dto.response.PreferenciaAdminResponse;
import com.playzone.pems.application.preferencia.port.in.ActualizarPreferenciaAdminUseCase;
import com.playzone.pems.application.preferencia.port.in.ObtenerPreferenciaAdminUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthContext;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.interfaces.rest.preferencia.request.ActualizarPreferenciaAdminRequest;
import com.playzone.pems.shared.exception.UnauthorizedException;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/preferencias/admin")
@RequiredArgsConstructor
public class PreferenciaAdminController {

    private final ObtenerPreferenciaAdminUseCase  obtenerUseCase;
    private final ActualizarPreferenciaAdminUseCase actualizarUseCase;
    private final SupabaseAuthFacade               authFacade;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> obtener() {
        UUID userId = userId();
        return ResponseEntity.ok(ApiResponse.ok(obtenerUseCase.obtener(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> actualizar(
            @RequestBody ActualizarPreferenciaAdminRequest request) {
        UUID userId = userId();
        ActualizarPreferenciaAdminCommand command = toCommand(request);
        return ResponseEntity.ok(ApiResponse.ok(actualizarUseCase.actualizar(userId, command)));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> parchear(
            @RequestBody Map<String, Object> patch) {
        UUID userId = userId();
        return ResponseEntity.ok(ApiResponse.ok(actualizarUseCase.parchear(userId, patch)));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<PreferenciaAdminResponse>> resetear() {
        UUID userId = userId();
        return ResponseEntity.ok(ApiResponse.ok(actualizarUseCase.resetear(userId)));
    }

    private UUID userId() {
        return authFacade.contextoActual()
                .map(SupabaseAuthContext::userId)
                .orElseThrow(() -> new UnauthorizedException("No autenticado"));
    }

    private ActualizarPreferenciaAdminCommand toCommand(ActualizarPreferenciaAdminRequest r) {
        return ActualizarPreferenciaAdminCommand.builder()
                .tema(r.getTema())
                .idioma(r.getIdioma())
                .zonaHoraria(r.getZonaHoraria())
                .formatoFecha(r.getFormatoFecha())
                .formatoHora(r.getFormatoHora())
                .sidebarColapsado(r.isSidebarColapsado())
                .autoRefreshDashboard(r.isAutoRefreshDashboard())
                .colorPrimario(r.getColorPrimario())
                .colorSecundario(r.getColorSecundario())
                .colorSidebar(r.getColorSidebar())
                .colorAcento(r.getColorAcento())
                .tipografia(r.getTipografia())
                .tamanioFuente(r.getTamanioFuente())
                .radiosBordes(r.getRadiosBordes())
                .sidebarFlotante(r.isSidebarFlotante())
                .modoCompacto(r.isModoCompacto())
                .anchoContenido(r.getAnchoContenido())
                .mostrarMigaspan(r.isMostrarMigaspan())
                .mostrarIconosMenu(r.isMostrarIconosMenu())
                .mostrarAnimaciones(r.isMostrarAnimaciones())
                .animacionSidebar(r.isAnimacionSidebar())
                .hoverEffects(r.isHoverEffects())
                .loadersAnimados(r.isLoadersAnimados())
                .confirmarAcciones(r.isConfirmarAcciones())
                .recordarUltimaPagina(r.isRecordarUltimaPagina())
                .restaurarTabs(r.isRestaurarTabs())
                .intervaloRefreshSeg(r.getIntervaloRefreshSeg())
                .dashboardPersonalizado(r.getDashboardPersonalizado())
                .widgetsVisibles(r.getWidgetsVisibles())
                .accesosRapidos(r.getAccesosRapidos())
                .ordenWidgets(r.getOrdenWidgets())
                .layoutDashboard(r.getLayoutDashboard())
                .filtrosPersistentes(r.getFiltrosPersistentes())
                .columnasVisibles(r.getColumnasVisibles())
                .ordenamientoTablas(r.getOrdenamientoTablas())
                .elementosPorTabla(r.getElementosPorTabla())
                .sonidoNotificaciones(r.isSonidoNotificaciones())
                .notificacionesPush(r.isNotificacionesPush())
                .notificacionesEmail(r.isNotificacionesEmail())
                .notificacionesVisuales(r.isNotificacionesVisuales())
                .badgesDinamicos(r.isBadgesDinamicos())
                .primerDiaSemana(r.getPrimerDiaSemana())
                .altoContraste(r.isAltoContraste())
                .reducirAnimaciones(r.isReducirAnimaciones())
                .aumentarEspaciado(r.isAumentarEspaciado())
                .cursorGrande(r.isCursorGrande())
                .build();
    }
}
