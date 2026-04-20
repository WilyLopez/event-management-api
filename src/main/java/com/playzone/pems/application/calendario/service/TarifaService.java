package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.command.ConfigurarTarifaCommand;
import com.playzone.pems.application.calendario.port.in.ConfigurarTarifaUseCase;
import com.playzone.pems.domain.calendario.model.Tarifa;
import com.playzone.pems.domain.calendario.repository.TarifaRepository;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TarifaService implements ConfigurarTarifaUseCase {

    private final TarifaRepository tarifaRepository;

    @Override
    @Transactional
    public Tarifa ejecutar(ConfigurarTarifaCommand command) {
        if (command.getVigenciaHasta() != null
                && command.getVigenciaHasta().isBefore(command.getVigenciaDesde())) {
            throw new ValidationException("vigenciaHasta", "La vigencia final no puede ser anterior a la inicial.");
        }

        tarifaRepository.desactivarAnterioresBySedeAndTipoDia(
                command.getIdSede(), command.getTipoDia());

        Tarifa tarifa = Tarifa.builder()
                .idSede(command.getIdSede())
                .tipoDia(command.getTipoDia())
                .precio(command.getPrecio())
                .vigenciaDesde(command.getVigenciaDesde())
                .vigenciaHasta(command.getVigenciaHasta())
                .activo(true)
                .build();

        return tarifaRepository.save(tarifa);
    }
}