package com.playzone.pems.application.configuracion.port.in;

import com.playzone.pems.domain.configuracion.model.ConfiguracionSistema;

import java.util.List;
import java.util.Map;

public interface GestionarConfiguracionUseCase {

    List<ConfiguracionSistema> listar();

    List<ConfiguracionSistema> actualizar(Map<String, String> cambios);
}
