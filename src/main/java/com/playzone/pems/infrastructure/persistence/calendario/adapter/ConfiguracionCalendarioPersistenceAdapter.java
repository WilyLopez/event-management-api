package com.playzone.pems.infrastructure.persistence.calendario.adapter;

import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;
import com.playzone.pems.domain.calendario.repository.ConfiguracionCalendarioRepository;
import com.playzone.pems.infrastructure.persistence.calendario.entity.ConfiguracionCalendarioEntity;
import com.playzone.pems.infrastructure.persistence.calendario.jpa.ConfiguracionCalendarioJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfiguracionCalendarioPersistenceAdapter implements ConfiguracionCalendarioRepository {

    private final ConfiguracionCalendarioJpaRepository configJpa;
    private final SedeJpaRepository                    sedeJpa;

    @Override
    public ConfiguracionCalendario obtener(Long idSede) {
        ConfiguracionCalendarioEntity e = configJpa.findBySede_Id(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("ConfiguracionCalendario", idSede));
        return toDomain(e);
    }

    @Override
    public ConfiguracionCalendario save(ConfiguracionCalendario config) {
        ConfiguracionCalendarioEntity entity = configJpa.findBySede_Id(config.getIdSede())
                .orElseGet(() -> {
                    var sede = sedeJpa.findById(config.getIdSede())
                            .orElseThrow(() -> new ResourceNotFoundException("Sede", config.getIdSede()));
                    return ConfiguracionCalendarioEntity.builder().sede(sede).build();
                });

        entity.setDiasMinReservaPublica(config.getDiasMinReservaPublica());
        entity.setDiasMaxReservaPublica(config.getDiasMaxReservaPublica());
        entity.setDiasMinEventoPrivado(config.getDiasMinEventoPrivado());
        entity.setDiasMaxEventoPrivado(config.getDiasMaxEventoPrivado());
        entity.setAforoMaximo(config.getAforoMaximo());
        entity.setHoraApertura(config.getHoraApertura());
        entity.setHoraCierre(config.getHoraCierre());
        entity.setTurnoT1Inicio(config.getTurnoT1Inicio());
        entity.setTurnoT1Fin(config.getTurnoT1Fin());
        entity.setTurnoT2Inicio(config.getTurnoT2Inicio());
        entity.setTurnoT2Fin(config.getTurnoT2Fin());
        entity.setDiasOperacion(config.getDiasOperacion());
        entity.setRangoMaxBloqueo(config.getRangoMaxBloqueo());

        return toDomain(configJpa.save(entity));
    }

    private ConfiguracionCalendario toDomain(ConfiguracionCalendarioEntity e) {
        return ConfiguracionCalendario.builder()
                .idConfig(e.getId())
                .idSede(e.getSede().getId())
                .diasMinReservaPublica(e.getDiasMinReservaPublica())
                .diasMaxReservaPublica(e.getDiasMaxReservaPublica())
                .diasMinEventoPrivado(e.getDiasMinEventoPrivado())
                .diasMaxEventoPrivado(e.getDiasMaxEventoPrivado())
                .aforoMaximo(e.getAforoMaximo())
                .horaApertura(e.getHoraApertura())
                .horaCierre(e.getHoraCierre())
                .turnoT1Inicio(e.getTurnoT1Inicio())
                .turnoT1Fin(e.getTurnoT1Fin())
                .turnoT2Inicio(e.getTurnoT2Inicio())
                .turnoT2Fin(e.getTurnoT2Fin())
                .diasOperacion(e.getDiasOperacion())
                .rangoMaxBloqueo(e.getRangoMaxBloqueo())
                .build();
    }
}
