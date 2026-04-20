package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.command.BloquearFechasCommand;
import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;
import com.playzone.pems.application.calendario.port.in.BloquearFechasUseCase;
import com.playzone.pems.application.calendario.port.in.ConsultarDisponibilidadUseCase;
import com.playzone.pems.domain.calendario.exception.DisponibilidadNotFoundException;
import com.playzone.pems.domain.calendario.model.BloqueCalendario;
import com.playzone.pems.domain.calendario.model.DisponibilidadDiaria;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.calendario.repository.BloqueCalendarioRepository;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarioService
        implements ConsultarDisponibilidadUseCase,
        BloquearFechasUseCase {

    private final DisponibilidadDiariaRepository disponibilidadRepository;
    private final BloqueCalendarioRepository     bloqueRepository;
    private final FeriadoRepository              feriadoRepository;

    @Value("${playzone.negocio.aforo-maximo:60}")
    private int aforoMaximo;

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadQuery consultarPorFecha(Long idSede, LocalDate fecha) {
        DisponibilidadDiaria disp = disponibilidadRepository
                .findBySedeAndFecha(idSede, fecha)
                .orElseThrow(() -> new DisponibilidadNotFoundException(idSede, fecha));

        boolean bloqueado = bloqueRepository.existsBloqueActivoEnFecha(idSede, fecha);
        TipoDia tipoDia   = resolverTipoDia(fecha);

        return toQuery(disp, tipoDia, bloqueado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadQuery> consultarRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return disponibilidadRepository
                .findBySedeAndFechasBetween(idSede, inicio, fin)
                .stream()
                .map(d -> {
                    boolean bloqueado = bloqueRepository.existsBloqueActivoEnFecha(idSede, d.getFecha());
                    return toQuery(d, resolverTipoDia(d.getFecha()), bloqueado);
                })
                .toList();
    }

    @Override
    @Transactional
    public BloqueCalendario ejecutar(BloquearFechasCommand command) {
        if (command.getFechaFin().isBefore(command.getFechaInicio())) {
            throw new ValidationException("fechaFin", "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (bloqueRepository.existsSolapamientoEnRango(
                command.getIdSede(), command.getFechaInicio(), command.getFechaFin())) {
            throw new ValidationException("El rango de fechas se solapa con un bloqueo existente.");
        }

        BloqueCalendario bloque = BloqueCalendario.builder()
                .idSede(command.getIdSede())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .motivo(command.getMotivo())
                .activo(true)
                .build();

        return bloqueRepository.save(bloque);
    }

    @Override
    @Transactional
    public void desactivar(Long idBloque) {
        bloqueRepository.desactivar(idBloque);
    }

    private TipoDia resolverTipoDia(LocalDate fecha) {
        boolean esFeriado = feriadoRepository.findByFecha(fecha).isPresent();
        return (FechaUtil.esFindeSemana(fecha) || esFeriado)
                ? TipoDia.FIN_SEMANA_FERIADO
                : TipoDia.SEMANA;
    }

    private DisponibilidadQuery toQuery(DisponibilidadDiaria d, TipoDia tipoDia, boolean bloqueado) {
        return DisponibilidadQuery.builder()
                .idSede(d.getIdSede())
                .fecha(d.getFecha())
                .accesoPublicoActivo(d.isAccesoPublicoActivo())
                .turnoT1Disponible(d.isTurnoT1Disponible())
                .turnoT2Disponible(d.isTurnoT2Disponible())
                .aforoPublicoActual(d.getAforoPublicoActual())
                .aforoMaximo(aforoMaximo)
                .plazasDisponibles(d.plazasDisponibles(aforoMaximo))
                .aforoCompleto(!d.admiteReservaPublica(aforoMaximo))
                .bloqueadoManualmente(bloqueado)
                .tipoDia(tipoDia.getCodigo())
                .build();
    }
}