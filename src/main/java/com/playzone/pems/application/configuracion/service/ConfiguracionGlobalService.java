package com.playzone.pems.application.configuracion.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.configuracion.port.in.GestionarConfiguracionUseCase;
import com.playzone.pems.domain.configuracion.model.ConfiguracionGlobal;
import com.playzone.pems.domain.configuracion.repository.ConfiguracionGlobalRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfiguracionGlobalService implements GestionarConfiguracionUseCase {

    private final ConfiguracionGlobalRepository configuracionRepository;
    private final SupabaseAuthFacade            authFacade;
    private final RegistrarLogUseCase           auditoria;

    @Override
    public List<ConfiguracionGlobal> listar() {
        return configuracionRepository.findAll();
    }

    @Override
    @Transactional
    public List<ConfiguracionGlobal> actualizar(Map<String, String> cambios) {
        List<ConfiguracionGlobal> pendientes = new ArrayList<>();
        for (Map.Entry<String, String> entry : cambios.entrySet()) {
            ConfiguracionGlobal config = configuracionRepository.findByClave(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Configuracion", "clave", entry.getKey()));
            pendientes.add(config.toBuilder().valor(entry.getValue()).build());
        }
        List<ConfiguracionGlobal> resultado = configuracionRepository.saveAll(pendientes);

        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_CONFIGURACION,
                "ConfiguracionGlobal", null,
                null, cambios.keySet().toString(),
                "Configuración global actualizada: " + cambios.size() + " clave(s)",
                null, null, AuditoriaConstants.NIVEL_WARNING, AuditoriaConstants.RESULTADO_EXITOSO));

        return resultado;
    }
}
