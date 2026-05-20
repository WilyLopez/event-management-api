package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.query.*;
import com.playzone.pems.application.finanzas.port.in.ConsultarResumenFinancieroUseCase;
import com.playzone.pems.domain.finanzas.repository.GastoEventoPrivadoRepository;
import com.playzone.pems.domain.finanzas.repository.GastoOperativoDiarioRepository;
import com.playzone.pems.domain.finanzas.repository.RegistroEgresoRepository;
import com.playzone.pems.domain.finanzas.repository.TipoEgresoRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.ContratoProveedorJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumenFinancieroService implements ConsultarResumenFinancieroUseCase {

    private final ReservaPublicaJpaRepository    reservaJpaRepository;
    private final EventoPrivadoJpaRepository     eventoJpaRepository;
    private final ContratoProveedorJpaRepository contratoProveedorJpaRepository;
    private final RegistroEgresoRepository       registroEgresoRepository;
    private final GastoEventoPrivadoRepository   gastoEventoRepository;
    private final GastoOperativoDiarioRepository gastoOperativoRepository;
    private final TipoEgresoRepository           tipoEgresoRepository;

    @Override
    public ResumenFinancieroQuery resumenMensual(Long idSede, int anio, int mes) {
        BigDecimal ingresoReservas = reservaJpaRepository
                .sumIngresosBySedeAndPeriodo(idSede, anio, mes);
        BigDecimal ingresoEventos = eventoJpaRepository
                .sumAdelantosBySedeAndPeriodo(idSede, anio, mes);
        BigDecimal ingresoOtros   = BigDecimal.ZERO;
        BigDecimal ingresoGeneral = ingresoReservas.add(ingresoEventos).add(ingresoOtros);

        BigDecimal egresoGeneral  = registroEgresoRepository.sumMontoBySedeAndPeriodo(idSede, anio, mes);
        BigDecimal egresoOperativo = gastoOperativoRepository.sumMontoBySedeAndPeriodo(idSede, anio, mes);

        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin    = YearMonth.of(anio, mes).atEndOfMonth();
        List<EventoPrivadoEntity> eventosDelMes = eventoJpaRepository
                .findBySede_IdAndFechaEventoBetween(idSede, inicio, fin);
        BigDecimal egresoEventos = eventosDelMes.stream()
                .map(e -> gastoEventoRepository.sumMontoByEvento(e.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresoNeto    = egresoGeneral.add(egresoOperativo).add(egresoEventos);
        BigDecimal utilidadNeta  = ingresoGeneral.subtract(egresoNeto);

        List<DesgloseTipoEgresoQuery> desglose = tipoEgresoRepository.findAllActivos().stream()
                .map(tipo -> {
                    BigDecimal totalTipo = registroEgresoRepository
                            .findBySedeAndPeriodo(idSede, anio, mes).stream()
                            .filter(r -> r.getIdTipoEgreso().equals(tipo.getId()))
                            .map(r -> r.getMonto())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return DesgloseTipoEgresoQuery.builder()
                            .nombreTipo(tipo.getNombre())
                            .categoria(tipo.getCategoria())
                            .totalMonto(totalTipo)
                            .build();
                })
                .filter(d -> d.getTotalMonto().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        return ResumenFinancieroQuery.builder()
                .anio(anio)
                .mes(mes)
                .totalIngresoReservas(ingresoReservas)
                .totalIngresoEventos(ingresoEventos)
                .totalIngresoOtros(ingresoOtros)
                .totalIngresoGeneral(ingresoGeneral)
                .totalEgresoGeneral(egresoGeneral)
                .totalEgresoEventos(egresoEventos)
                .totalEgresoOperativo(egresoOperativo)
                .totalEgresoNeto(egresoNeto)
                .utilidadNeta(utilidadNeta)
                .desglosePorTipoEgreso(desglose)
                .build();
    }

    @Override
    public ResumenEventoFinancieroQuery resumenEvento(Long idEvento) {
        EventoPrivadoEntity evento = eventoJpaRepository.findById(idEvento)
                .orElseThrow(() -> new ResourceNotFoundException("Evento privado no encontrado."));

        BigDecimal ingresoContrato = evento.getPrecioTotalContrato() != null
                ? evento.getPrecioTotalContrato() : BigDecimal.ZERO;
        BigDecimal montoAdelanto   = evento.getMontoAdelanto() != null
                ? evento.getMontoAdelanto() : BigDecimal.ZERO;

        BigDecimal gastosProveedores = contratoProveedorJpaRepository
                .sumMontoAcordadoByEventoAndContratadoPorEmpresa(idEvento);
        BigDecimal gastosAdicionales = gastoEventoRepository.sumMontoByEvento(idEvento);
        BigDecimal totalGastos       = gastosProveedores.add(gastosAdicionales);
        BigDecimal utilidadBruta     = ingresoContrato.subtract(totalGastos);

        return ResumenEventoFinancieroQuery.builder()
                .idEvento(evento.getId())
                .tipoEvento(evento.getTipoEvento())
                .nombreCliente(evento.getCliente().getNombre())
                .fechaEvento(evento.getFechaEvento())
                .ingresoContrato(ingresoContrato)
                .montoAdelanto(montoAdelanto)
                .totalGastosProveedores(gastosProveedores)
                .totalGastosAdicionales(gastosAdicionales)
                .totalGastos(totalGastos)
                .utilidadBruta(utilidadBruta)
                .build();
    }

    @Override
    public List<ResumenDiarioFinancieroQuery> resumenDiario(Long idSede, LocalDate inicio, LocalDate fin) {
        List<ResumenDiarioFinancieroQuery> resultado = new ArrayList<>();
        LocalDate cursor = inicio;
        while (!cursor.isAfter(fin)) {
            BigDecimal ingresoReservas = reservaJpaRepository.sumIngresosBySedeAndFecha(idSede, cursor);
            BigDecimal gastoOperativo  = gastoOperativoRepository.sumMontoBySedeAndFecha(idSede, cursor);
            int cantidadReservas       = reservaJpaRepository.countConfirmadasBySedeAndFecha(idSede, cursor);
            BigDecimal utilidadDia     = ingresoReservas.subtract(gastoOperativo);
            resultado.add(ResumenDiarioFinancieroQuery.builder()
                    .fecha(cursor)
                    .ingresoReservas(ingresoReservas)
                    .gastoOperativo(gastoOperativo)
                    .utilidadDia(utilidadDia)
                    .cantidadReservas(cantidadReservas)
                    .build());
            cursor = cursor.plusDays(1);
        }
        return resultado;
    }
}
