package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.calendario.port.in.ConfiguracionCalendarioUseCase;
import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfiguracionCalendarioService implements ConfiguracionCalendarioUseCase {

    private final ConfiguracionCalendarioRepository configRepository;
    private final SupabaseAuthFacade                authFacade;
    private final RegistrarLogUseCase               auditoria;

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionCalendario obtener(Long idSede) {
        return configRepository.obtener(idSede);
    }

    @Override
    @Transactional
    public ConfiguracionCalendario actualizar(Long idSede, ConfiguracionCalendario config) {
        if (config.getDiasMaxReservaPublica() < config.getDiasMinReservaPublica()) {
            throw new ValidationException("El maximo de anticipacion publica debe ser mayor o igual al minimo.");
        }
        if (config.getDiasMaxEventoPrivado() < config.getDiasMinEventoPrivado()) {
            throw new ValidationException("El maximo de anticipacion de evento privado debe ser mayor o igual al minimo.");
        }
        if (config.getAforoMaximo() <= 0) {
            throw new ValidationException("El aforo maximo debe ser mayor a 0.");
        }
        if (!config.getHoraCierre().isAfter(config.getHoraApertura())) {
            throw new ValidationException("La hora de cierre debe ser posterior a la hora de apertura.");
        }
        if (config.getEdadMaxCumple() < config.getEdadMinCumple()) {
            throw new ValidationException("La edad maxima debe ser mayor o igual a la edad minima.");
        }
        ConfiguracionCalendario resultado = configRepository.save(config.toBuilder().idSede(idSede).build());

        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_CALENDARIO,
                "ConfiguracionCalendario", idSede,
                null, "sede=" + idSede,
                "Configuración de calendario actualizada para sede " + idSede,
                null, null, AuditoriaConstants.NIVEL_WARNING, AuditoriaConstants.RESULTADO_EXITOSO));

        return resultado;
    }
}
