package com.playzone.pems.interfaces.rest.marketing;

import com.playzone.pems.application.marketing.dto.command.CrearCampanaCommand;
import com.playzone.pems.application.marketing.dto.command.FiltroDestinatariosCommand;
import com.playzone.pems.application.marketing.dto.command.GuardarPlantillaCommand;
import com.playzone.pems.application.marketing.dto.query.CampanaEmailQuery;
import com.playzone.pems.application.marketing.dto.query.EnvioEmailQuery;
import com.playzone.pems.application.marketing.dto.query.PlantillaEmailQuery;
import com.playzone.pems.application.marketing.dto.query.TipoEmailQuery;
import com.playzone.pems.application.marketing.port.in.CrearCampanaEmailUseCase;
import com.playzone.pems.application.marketing.port.in.CrearPlantillaEmailUseCase;
import com.playzone.pems.application.marketing.port.in.EnviarCampanaUseCase;
import com.playzone.pems.application.marketing.port.in.ListarCampanasUseCase;
import com.playzone.pems.application.marketing.port.in.ListarEnviosUseCase;
import com.playzone.pems.application.marketing.port.in.ListarPlantillasUseCase;
import com.playzone.pems.application.marketing.service.MarketingService;
import com.playzone.pems.interfaces.rest.marketing.request.CrearCampanaRequest;
import com.playzone.pems.interfaces.rest.marketing.request.EnviarCampanaRequest;
import com.playzone.pems.interfaces.rest.marketing.request.GuardarPlantillaRequest;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marketing")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MarketingController {

    private final CrearPlantillaEmailUseCase crearPlantillaUseCase;
    private final ListarPlantillasUseCase    listarPlantillasUseCase;
    private final CrearCampanaEmailUseCase   crearCampanaUseCase;
    private final ListarCampanasUseCase      listarCampanasUseCase;
    private final EnviarCampanaUseCase       enviarCampanaUseCase;
    private final ListarEnviosUseCase        listarEnviosUseCase;
    private final MarketingService           marketingService;

    @GetMapping("/tipos-email")
    public ResponseEntity<ApiResponse<List<TipoEmailQuery>>> listarTipos() {
        return ResponseEntity.ok(ApiResponse.ok(marketingService.listarTipos()));
    }

    @PostMapping("/plantillas")
    public ResponseEntity<ApiResponse<PlantillaEmailQuery>> crearPlantilla(
            @Valid @RequestBody GuardarPlantillaRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal) {

        Long idUsuario = extraerIdUsuario(principal);
        PlantillaEmailQuery query = crearPlantillaUseCase.ejecutar(
                GuardarPlantillaCommand.builder()
                        .idTipoEmail(request.getIdTipoEmail())
                        .nombre(request.getNombre())
                        .asunto(request.getAsunto())
                        .contenidoHtml(request.getContenidoHtml())
                        .contenidoFallback(request.getContenidoFallback())
                        .variablesPermitidas(request.getVariablesPermitidas())
                        .build(),
                idUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(query));
    }

    @GetMapping("/plantillas")
    public ResponseEntity<ApiResponse<List<PlantillaEmailQuery>>> listarPlantillas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        List<PlantillaEmailQuery> lista = listarPlantillasUseCase.listarPlantillas(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaActualizacion")));

        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @PostMapping("/campanas")
    public ResponseEntity<ApiResponse<CampanaEmailQuery>> crearCampana(
            @Valid @RequestBody CrearCampanaRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal) {

        Long idUsuario = extraerIdUsuario(principal);
        CampanaEmailQuery query = crearCampanaUseCase.ejecutar(
                CrearCampanaCommand.builder()
                        .nombre(request.getNombre())
                        .descripcion(request.getDescripcion())
                        .idPlantillaEmail(request.getIdPlantillaEmail())
                        .fechaProgramada(request.getFechaProgramada())
                        .build(),
                idUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(query));
    }

    @GetMapping("/campanas")
    public ResponseEntity<ApiResponse<PagedResponse<CampanaEmailQuery>>> listarCampanas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size) {

        PagedResponse<CampanaEmailQuery> resultado = listarCampanasUseCase.listarCampanas(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion")));

        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @PostMapping("/campanas/{id}/enviar")
    public ResponseEntity<ApiResponse<Void>> enviarCampana(
            @PathVariable Long id,
            @RequestBody(required = false) EnviarCampanaRequest request) {

        FiltroDestinatariosCommand filtro = request != null
                ? FiltroDestinatariosCommand.builder()
                        .soloVip(request.getSoloVip())
                        .soloFrecuentes(request.getSoloFrecuentes())
                        .soloNuevos(request.getSoloNuevos())
                        .soloInactivos(request.getSoloInactivos())
                        .soloCorporativos(request.getSoloCorporativos())
                        .soloConAccesoWeb(request.getSoloConAccesoWeb())
                        .soloPresenciales(request.getSoloPresenciales())
                        .build()
                : FiltroDestinatariosCommand.builder().build();

        enviarCampanaUseCase.ejecutar(id, filtro);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/campanas/{id}/envios")
    public ResponseEntity<ApiResponse<PagedResponse<EnvioEmailQuery>>> listarEnvios(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<EnvioEmailQuery> resultado = listarEnviosUseCase.ejecutar(
                id, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion")));

        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    private Long extraerIdUsuario(org.springframework.security.core.userdetails.UserDetails principal) {
        try {
            return Long.parseLong(principal.getUsername());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
