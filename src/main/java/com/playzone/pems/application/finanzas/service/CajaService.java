package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.AbrirCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.CerrarCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarMovimientoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.AperturaCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.MovimientoCajaQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarCajaUseCase;
import com.playzone.pems.domain.finanzas.model.AperturaCaja;
import com.playzone.pems.domain.finanzas.model.MovimientoCaja;
import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import com.playzone.pems.domain.finanzas.repository.AperturaCajaRepository;
import com.playzone.pems.domain.finanzas.repository.MovimientoCajaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CajaService implements GestionarCajaUseCase {

    private final AperturaCajaRepository  aperturaCajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    @Override
    public AperturaCajaQuery abrir(AbrirCajaCommand command) {
        aperturaCajaRepository.findBySedeAndFecha(command.getIdSede(), command.getFecha())
                .ifPresent(a -> {
                    throw new ValidationException("Ya existe una caja abierta para esa fecha.");
                });
        AperturaCaja apertura = AperturaCaja.builder()
                .idSede(command.getIdSede())
                .fecha(command.getFecha())
                .saldoInicial(command.getSaldoInicial() != null ? command.getSaldoInicial() : BigDecimal.ZERO)
                .totalIngresos(BigDecimal.ZERO)
                .totalEgresos(BigDecimal.ZERO)
                .estado(EstadoCaja.ABIERTA)
                .idUsuarioApertura(command.getIdUsuarioApertura())
                .fechaApertura(OffsetDateTime.now())
                .observaciones(command.getObservaciones())
                .build();
        return toQuery(aperturaCajaRepository.save(apertura));
    }

    @Override
    public AperturaCajaQuery cerrar(CerrarCajaCommand command) {
        AperturaCaja existente = aperturaCajaRepository.findById(command.getIdAperturaCaja())
                .orElseThrow(() -> new ResourceNotFoundException("Apertura de caja no encontrada."));
        if (existente.getEstado() == EstadoCaja.CERRADA) {
            throw new ValidationException("La caja ya está cerrada.");
        }
        AperturaCaja cerrada = AperturaCaja.builder()
                .id(existente.getId())
                .idSede(existente.getIdSede())
                .fecha(existente.getFecha())
                .saldoInicial(existente.getSaldoInicial())
                .saldoFinal(command.getSaldoFinal())
                .totalIngresos(existente.getTotalIngresos())
                .totalEgresos(existente.getTotalEgresos())
                .estado(EstadoCaja.CERRADA)
                .idUsuarioApertura(existente.getIdUsuarioApertura())
                .idUsuarioCierre(command.getIdUsuarioCierre())
                .fechaApertura(existente.getFechaApertura())
                .fechaCierre(OffsetDateTime.now())
                .observaciones(command.getObservaciones() != null
                        ? command.getObservaciones() : existente.getObservaciones())
                .build();
        return toQuery(aperturaCajaRepository.save(cerrada));
    }

    @Override
    @Transactional(readOnly = true)
    public AperturaCajaQuery obtenerPorSedeYFecha(Long idSede, LocalDate fecha) {
        return aperturaCajaRepository.findBySedeAndFecha(idSede, fecha)
                .map(this::toQuery)
                .orElseThrow(() -> new ResourceNotFoundException("No hay caja registrada para esa fecha."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AperturaCajaQuery> listarPorRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return aperturaCajaRepository.findBySedeAndRango(idSede, inicio, fin)
                .stream().map(this::toQuery).toList();
    }

    @Override
    public MovimientoCajaQuery registrarMovimiento(RegistrarMovimientoManualCommand command) {
        AperturaCaja apertura = aperturaCajaRepository.findById(command.getIdAperturaCaja())
                .orElseThrow(() -> new ResourceNotFoundException("Apertura de caja no encontrada."));
        if (apertura.getEstado() == EstadoCaja.CERRADA) {
            throw new ValidationException("No se puede registrar movimientos en una caja cerrada.");
        }
        MovimientoCaja movimiento = MovimientoCaja.builder()
                .idAperturaCaja(command.getIdAperturaCaja())
                .tipo(command.getTipo())
                .concepto(command.getConcepto())
                .monto(command.getMonto())
                .medioPago(command.getMedioPago())
                .esManual(true)
                .idUsuarioRegistra(command.getIdUsuarioRegistra())
                .build();
        return toMovimientoQuery(movimientoCajaRepository.save(movimiento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaQuery> listarMovimientos(Long idAperturaCaja) {
        return movimientoCajaRepository.findByApertura(idAperturaCaja)
                .stream().map(this::toMovimientoQuery).toList();
    }

    @Override
    public void eliminarMovimiento(Long idMovimiento) {
        movimientoCajaRepository.deleteById(idMovimiento);
    }

    private AperturaCajaQuery toQuery(AperturaCaja a) {
        return AperturaCajaQuery.builder()
                .id(a.getId())
                .idSede(a.getIdSede())
                .fecha(a.getFecha())
                .saldoInicial(a.getSaldoInicial())
                .saldoFinal(a.getSaldoFinal())
                .totalIngresos(a.getTotalIngresos())
                .totalEgresos(a.getTotalEgresos())
                .estado(a.getEstado())
                .idUsuarioApertura(a.getIdUsuarioApertura())
                .idUsuarioCierre(a.getIdUsuarioCierre())
                .fechaApertura(a.getFechaApertura())
                .fechaCierre(a.getFechaCierre())
                .observaciones(a.getObservaciones())
                .build();
    }

    private MovimientoCajaQuery toMovimientoQuery(MovimientoCaja m) {
        return MovimientoCajaQuery.builder()
                .id(m.getId())
                .idAperturaCaja(m.getIdAperturaCaja())
                .tipo(m.getTipo())
                .concepto(m.getConcepto())
                .monto(m.getMonto())
                .medioPago(m.getMedioPago())
                .idRegistroIngreso(m.getIdRegistroIngreso())
                .idRegistroEgreso(m.getIdRegistroEgreso())
                .idVenta(m.getIdVenta())
                .esManual(m.isEsManual())
                .fechaCreacion(m.getFechaCreacion())
                .build();
    }
}
