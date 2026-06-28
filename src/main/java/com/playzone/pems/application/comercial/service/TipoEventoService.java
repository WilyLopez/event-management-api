package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.comercial.dto.response.TipoEventoResponse;
import com.playzone.pems.application.comercial.port.in.GestionarTipoEventoUseCase;
import com.playzone.pems.domain.comercial.model.TipoEvento;
import com.playzone.pems.domain.comercial.repository.TipoEventoRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.BusinessException;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoEventoService implements GestionarTipoEventoUseCase {

    private final TipoEventoRepository repository;
    private final SupabaseAuthFacade   authFacade;
    private final RegistrarLogUseCase  auditoria;

    @Override
    @Transactional(readOnly = true)
    public List<TipoEventoResponse> listarTodos() {
        return repository.listarTodos().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoEventoResponse> listarActivos() {
        return repository.listarActivos().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoEventoResponse obtener(String codigo) {
        return toResponse(buscarOFallar(codigo));
    }

    @Override
    public TipoEventoResponse crear(Map<String, Object> datos) {
        String nombre = str(datos, "nombre");
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre es obligatorio.", HttpStatus.BAD_REQUEST);
        }
        if (repository.existePorNombre(nombre)) {
            throw new BusinessException("Ya existe un tipo de evento con el nombre: " + nombre, HttpStatus.CONFLICT);
        }
        String codigo = generarCodigo(nombre);
        if (repository.existePorCodigo(codigo)) {
            codigo = codigo + "_" + System.currentTimeMillis();
        }
        TipoEvento nuevo = TipoEvento.builder()
                .codigo(codigo)
                .nombre(nombre)
                .descripcion(str(datos, "descripcion"))
                .icono(str(datos, "icono"))
                .esSistema(false)
                .activo(bool(datos, "activo", true))
                .orden(num(datos, "orden", 0))
                .build();
        TipoEventoResponse resultado = toResponse(repository.guardar(nuevo));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_CREAR, AuditoriaConstants.MOD_COMERCIAL,
                "TipoEvento", null,
                null, resultado.getCodigo(),
                "Tipo de evento creado: " + resultado.getNombre(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
    }

    @Override
    public TipoEventoResponse actualizar(String codigo, Map<String, Object> datos) {
        TipoEvento existente = buscarOFallar(codigo);
        if (existente.isEsSistema()) {
            throw new BusinessException("Los tipos de evento de sistema no pueden ser modificados.", HttpStatus.CONFLICT);
        }
        String nuevoNombre = str(datos, "nombre");
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new BusinessException("El nombre es obligatorio.", HttpStatus.BAD_REQUEST);
        }
        if (repository.existePorNombreExcluyendo(nuevoNombre, codigo)) {
            throw new BusinessException("Ya existe un tipo de evento con el nombre: " + nuevoNombre, HttpStatus.CONFLICT);
        }
        TipoEvento actualizado = existente.toBuilder()
                .nombre(nuevoNombre)
                .descripcion(str(datos, "descripcion"))
                .icono(str(datos, "icono"))
                .activo(bool(datos, "activo", existente.isActivo()))
                .orden(num(datos, "orden", existente.getOrden()))
                .build();
        TipoEventoResponse resultado = toResponse(repository.guardar(actualizado));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_COMERCIAL,
                "TipoEvento", null,
                existente.getNombre(), resultado.getNombre(),
                "Tipo de evento actualizado: " + resultado.getCodigo(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
    }

    @Override
    public void eliminar(String codigo) {
        TipoEvento existente = buscarOFallar(codigo);
        if (existente.isEsSistema()) {
            throw new BusinessException("Los tipos de evento de sistema no pueden ser eliminados.", HttpStatus.CONFLICT);
        }
        if (repository.tienePaquetesAsociados(codigo)) {
            throw new BusinessException(
                    "No se puede eliminar: existen paquetes asociados a este tipo de evento.", HttpStatus.CONFLICT);
        }
        repository.eliminar(codigo);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ELIMINAR, AuditoriaConstants.MOD_COMERCIAL,
                "TipoEvento", null,
                existente.getNombre(), null,
                "Tipo de evento eliminado: " + codigo,
                null, null, AuditoriaConstants.NIVEL_CRITICAL, AuditoriaConstants.RESULTADO_EXITOSO));
    }

    private TipoEvento buscarOFallar(String codigo) {
        return repository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evento no encontrado: " + codigo));
    }

    private TipoEventoResponse toResponse(TipoEvento t) {
        return TipoEventoResponse.builder()
                .codigo(t.getCodigo())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .icono(t.getIcono())
                .esSistema(t.isEsSistema())
                .activo(t.isActivo())
                .orden(t.getOrden())
                .fechaCreacion(t.getCreatedAt())
                .fechaActualizacion(t.getUpdatedAt())
                .build();
    }

    private String generarCodigo(String nombre) {
        String normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalizado.toUpperCase()
                .replaceAll("[^A-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }

    private static String str(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v instanceof String s ? s.trim() : null;
    }

    private static boolean bool(Map<String, Object> m, String key, boolean def) {
        Object v = m.get(key);
        return v instanceof Boolean b ? b : def;
    }

    private static int num(Map<String, Object> m, String key, int def) {
        Object v = m.get(key);
        return v instanceof Number n ? n.intValue() : def;
    }
}
