package com.playzone.pems.application.configuracion.service;

import com.playzone.pems.application.configuracion.port.in.GestionarConfiguracionUseCase;
import com.playzone.pems.domain.configuracion.model.ConfiguracionSistema;
import com.playzone.pems.domain.configuracion.repository.ConfiguracionSistemaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfiguracionSistemaService implements GestionarConfiguracionUseCase {

    private final ConfiguracionSistemaRepository configuracionRepository;

    @Override
    public List<ConfiguracionSistema> listar() {
        return configuracionRepository.findAll();
    }

    @Override
    @Transactional
    public List<ConfiguracionSistema> actualizar(Map<String, String> cambios) {
        List<ConfiguracionSistema> pendientes = new ArrayList<>();
        for (Map.Entry<String, String> entry : cambios.entrySet()) {
            ConfiguracionSistema config = configuracionRepository.findByClave(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Configuracion", "clave", entry.getKey()));
            pendientes.add(config.toBuilder().valor(entry.getValue()).build());
        }
        return configuracionRepository.saveAll(pendientes);
    }
}
