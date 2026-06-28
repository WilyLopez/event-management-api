package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.query.DashboardFinancieroQuery;
import com.playzone.pems.application.finanzas.dto.query.SerieDiaQuery;
import com.playzone.pems.application.finanzas.port.in.ConsultarDashboardFinancieroUseCase;
import com.playzone.pems.domain.finanzas.query.MontoPorDia;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import com.playzone.pems.domain.finanzas.repository.RegistroEgresoRepository;
import com.playzone.pems.domain.finanzas.repository.RegistroIngresoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardFinancieroService implements ConsultarDashboardFinancieroUseCase {

    private final RegistroIngresoRepository  registroIngresoRepository;
    private final RegistroEgresoRepository   registroEgresoRepository;
    private final ReservaPublicaRepository   reservaPublicaRepository;
    private final EventoPrivadoRepository    eventoPrivadoRepository;

    @Override
    public DashboardFinancieroQuery consultar(Long idSede, int anio, int mes) {
        Map<String, BigDecimal> ingresosPorTipo =
                registroIngresoRepository.sumMontoAgrupadoPorTipo(idSede, anio, mes);

        BigDecimal ingresoReservas  = ingresosPorTipo.getOrDefault("RESERVA_PUBLICA",  BigDecimal.ZERO);
        BigDecimal ingresoAdelantos = ingresosPorTipo.getOrDefault("ADELANTO_EVENTO",  BigDecimal.ZERO);
        BigDecimal ingresoManual    = ingresosPorTipo.getOrDefault("INGRESO_MANUAL",   BigDecimal.ZERO)
                                        .add(ingresosPorTipo.getOrDefault("OTRO",      BigDecimal.ZERO));
        BigDecimal totalIngresos    = ingresosPorTipo.values().stream()
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<CategoriaEgreso, BigDecimal> egresosPorCategoria =
                registroEgresoRepository.sumMontoAgrupadoPorCategoria(idSede, anio, mes);

        BigDecimal egresoFijo       = egresosPorCategoria.getOrDefault(CategoriaEgreso.RECURRENTE_FIJO,     BigDecimal.ZERO);
        BigDecimal egresoVariable   = egresosPorCategoria.getOrDefault(CategoriaEgreso.RECURRENTE_VARIABLE, BigDecimal.ZERO);
        BigDecimal egresoEventual   = egresosPorCategoria.getOrDefault(CategoriaEgreso.EVENTUAL,            BigDecimal.ZERO);
        BigDecimal totalEgresos     = egresoFijo.add(egresoVariable).add(egresoEventual);

        BigDecimal utilidadNeta     = totalIngresos.subtract(totalEgresos);

        long reservasConfirmadas    = reservaPublicaRepository.countConfirmadasBySedeAndPeriodo(idSede, anio, mes);
        long reservasCanceladas     = reservaPublicaRepository.countCanceladasBySedeAndPeriodo(idSede, anio, mes);
        BigDecimal ticketPromedio   = reservaPublicaRepository.avgTicketBySedeAndPeriodo(idSede, anio, mes);
        BigDecimal saldoPendiente   = eventoPrivadoRepository.sumSaldoPendienteBySedeAndMes(idSede, anio, mes);

        YearMonth anterior = YearMonth.of(anio, mes).minusMonths(1);
        BigDecimal totalIngresosAnt = registroIngresoRepository
                .sumMontoAgrupadoPorTipo(idSede, anterior.getYear(), anterior.getMonthValue())
                .values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEgresosAnt  = registroEgresoRepository
                .sumMontoAgrupadoPorCategoria(idSede, anterior.getYear(), anterior.getMonthValue())
                .values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal utilidadAnt      = totalIngresosAnt.subtract(totalEgresosAnt);

        YearMonth periodo = YearMonth.of(anio, mes);
        LocalDate inicio  = periodo.atDay(1);
        LocalDate fin     = periodo.atEndOfMonth();

        Map<LocalDate, BigDecimal> ingresosPorDia = registroIngresoRepository
                .sumMontoAgrupadoPorDia(idSede, inicio, fin).stream()
                .collect(Collectors.toMap(MontoPorDia::fecha, MontoPorDia::monto));
        Map<LocalDate, BigDecimal> egresosPorDia = registroEgresoRepository
                .sumMontoAgrupadoPorDia(idSede, inicio, fin).stream()
                .collect(Collectors.toMap(MontoPorDia::fecha, MontoPorDia::monto));

        Map<LocalDate, SerieDiaQuery> serie = new TreeMap<>();
        ingresosPorDia.keySet().forEach(f -> serie.put(f, null));
        egresosPorDia.keySet().forEach(f -> serie.put(f, null));
        List<SerieDiaQuery> serieDiaria = serie.keySet().stream()
                .map(f -> new SerieDiaQuery(
                        f,
                        ingresosPorDia.getOrDefault(f, BigDecimal.ZERO),
                        egresosPorDia.getOrDefault(f, BigDecimal.ZERO)))
                .toList();

        return DashboardFinancieroQuery.builder()
                .anio(anio)
                .mes(mes)
                .totalIngresos(totalIngresos)
                .totalEgresos(totalEgresos)
                .utilidadNeta(utilidadNeta)
                .ingresoReservas(ingresoReservas)
                .ingresoAdelantos(ingresoAdelantos)
                .ingresoManual(ingresoManual)
                .egresoFijo(egresoFijo)
                .egresoVariable(egresoVariable)
                .egresoEventual(egresoEventual)
                .reservasConfirmadas(reservasConfirmadas)
                .reservasCanceladas(reservasCanceladas)
                .ticketPromedio(ticketPromedio)
                .saldoPendienteEventos(saldoPendiente)
                .totalIngresosMesAnterior(totalIngresosAnt)
                .totalEgresosMesAnterior(totalEgresosAnt)
                .utilidadMesAnterior(utilidadAnt)
                .serieDiaria(serieDiaria)
                .build();
    }
}
