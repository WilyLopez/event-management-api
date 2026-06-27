package com.playzone.pems.application.finanzas.service;

import com.playzone.pems.application.finanzas.dto.command.AbrirCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.CerrarCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarArqueoCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarMovimientoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.AperturaCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.ArqueoCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.MovimientoCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.ResumenCajaQuery;
import com.playzone.pems.application.finanzas.port.in.GestionarCajaUseCase;
import com.playzone.pems.domain.finanzas.model.AperturaCaja;
import com.playzone.pems.domain.finanzas.model.ArqueoCaja;
import com.playzone.pems.domain.finanzas.model.MovimientoCaja;
import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import com.playzone.pems.domain.finanzas.model.enums.TipoMovimientoCaja;
import com.playzone.pems.domain.finanzas.repository.AperturaCajaRepository;
import com.playzone.pems.domain.finanzas.repository.ArqueoCajaRepository;
import com.playzone.pems.domain.finanzas.repository.MovimientoCajaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CajaService implements GestionarCajaUseCase {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final AperturaCajaRepository   aperturaCajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final ArqueoCajaRepository     arqueoCajaRepository;

    @Override
    public AperturaCajaQuery abrir(AbrirCajaCommand command) {
        aperturaCajaRepository.findBySedeAndFecha(command.getIdSede(), command.getFecha())
                .ifPresent(a -> {
                    throw new ValidationException("Ya existe una caja registrada para esa fecha.");
                });

        aperturaCajaRepository.findAbiertaBySede(command.getIdSede())
                .ifPresent(a -> {
                    throw new ValidationException(
                            "Hay una caja del " + a.getFecha() + " que aún no ha sido cerrada. " +
                            "Cierre esa caja antes de abrir una nueva.");
                });

        AperturaCaja apertura = AperturaCaja.builder()
                .idSede(command.getIdSede())
                .fecha(command.getFecha())
                .saldoInicial(command.getSaldoInicial() != null ? command.getSaldoInicial() : BigDecimal.ZERO)
                .totalIngresos(BigDecimal.ZERO)
                .totalEgresos(BigDecimal.ZERO)
                .estado(EstadoCaja.ABIERTA)
                .idUsuarioApertura(command.getIdUsuarioApertura())
                .fechaApertura(OffsetDateTime.now(LIMA))
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

        BigDecimal saldoFinal   = command.getSaldoFinal() != null ? command.getSaldoFinal() : BigDecimal.ZERO;
        BigDecimal saldoEsperado = existente.getSaldoInicial()
                .add(existente.getTotalIngresos())
                .subtract(existente.getTotalEgresos());
        BigDecimal diferencia   = saldoFinal.subtract(saldoEsperado);

        AperturaCaja cerrada = AperturaCaja.builder()
                .id(existente.getId())
                .idSede(existente.getIdSede())
                .fecha(existente.getFecha())
                .saldoInicial(existente.getSaldoInicial())
                .saldoFinal(saldoFinal)
                .totalIngresos(existente.getTotalIngresos())
                .totalEgresos(existente.getTotalEgresos())
                .saldoEsperado(saldoEsperado)
                .diferencia(diferencia)
                .estado(EstadoCaja.CERRADA)
                .idUsuarioApertura(existente.getIdUsuarioApertura())
                .idUsuarioCierre(command.getIdUsuarioCierre())
                .fechaApertura(existente.getFechaApertura())
                .fechaCierre(OffsetDateTime.now(LIMA))
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
    public AperturaCajaQuery obtenerHoy(Long idSede) {
        LocalDate hoy = LocalDate.now(LIMA);
        return aperturaCajaRepository.findBySedeAndFecha(idSede, hoy)
                .map(this::toQuery)
                .orElseThrow(() -> new ResourceNotFoundException("No hay caja abierta para hoy."));
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
        MovimientoCaja guardado = movimientoCajaRepository.save(movimiento);

        if (command.getTipo() == TipoMovimientoCaja.INGRESO) {
            aperturaCajaRepository.incrementarIngresos(apertura.getId(), command.getMonto());
        } else {
            aperturaCajaRepository.incrementarEgresos(apertura.getId(), command.getMonto());
        }

        return toMovimientoQuery(guardado);
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

    @Override
    public ArqueoCajaQuery registrarArqueo(RegistrarArqueoCommand command) {
        AperturaCaja apertura = aperturaCajaRepository.findById(command.getIdAperturaCaja())
                .orElseThrow(() -> new ResourceNotFoundException("Apertura de caja no encontrada."));
        if (apertura.getEstado() == EstadoCaja.CERRADA) {
            throw new ValidationException("No se puede registrar un arqueo en una caja cerrada.");
        }

        BigDecimal saldoEsperado = apertura.getSaldoInicial()
                .add(apertura.getTotalIngresos())
                .subtract(apertura.getTotalEgresos());
        BigDecimal diferencia = command.getSaldoContado().subtract(saldoEsperado);

        ArqueoCaja arqueo = ArqueoCaja.builder()
                .idAperturaCaja(command.getIdAperturaCaja())
                .saldoEsperado(saldoEsperado)
                .saldoContado(command.getSaldoContado())
                .diferencia(diferencia)
                .observaciones(command.getObservaciones())
                .realizadoPor(command.getRealizadoPor())
                .build();
        return toArqueoQuery(arqueoCajaRepository.save(arqueo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArqueoCajaQuery> listarArqueos(Long idAperturaCaja) {
        return arqueoCajaRepository.findByApertura(idAperturaCaja)
                .stream().map(this::toArqueoQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenCajaQuery generarResumen(Long idAperturaCaja) {
        AperturaCaja apertura = aperturaCajaRepository.findById(idAperturaCaja)
                .orElseThrow(() -> new ResourceNotFoundException("Apertura de caja no encontrada."));

        BigDecimal saldoEsperado = apertura.getSaldoInicial()
                .add(apertura.getTotalIngresos())
                .subtract(apertura.getTotalEgresos());

        List<MovimientoCajaQuery> movimientos = movimientoCajaRepository.findByApertura(idAperturaCaja)
                .stream().map(this::toMovimientoQuery).toList();

        List<ArqueoCajaQuery> arqueos = arqueoCajaRepository.findByApertura(idAperturaCaja)
                .stream().map(this::toArqueoQuery).toList();

        return ResumenCajaQuery.builder()
                .id(apertura.getId())
                .idSede(apertura.getIdSede())
                .fecha(apertura.getFecha())
                .saldoInicial(apertura.getSaldoInicial())
                .totalIngresos(apertura.getTotalIngresos())
                .totalEgresos(apertura.getTotalEgresos())
                .saldoEsperado(apertura.getSaldoEsperado() != null ? apertura.getSaldoEsperado() : saldoEsperado)
                .saldoFinal(apertura.getSaldoFinal())
                .diferencia(apertura.getDiferencia())
                .estado(apertura.getEstado())
                .fechaApertura(apertura.getFechaApertura())
                .fechaCierre(apertura.getFechaCierre())
                .observaciones(apertura.getObservaciones())
                .movimientos(movimientos)
                .arqueos(arqueos)
                .build();
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
                .saldoEsperado(a.getSaldoEsperado())
                .diferencia(a.getDiferencia())
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

    private ArqueoCajaQuery toArqueoQuery(ArqueoCaja a) {
        return ArqueoCajaQuery.builder()
                .id(a.getId())
                .idAperturaCaja(a.getIdAperturaCaja())
                .saldoEsperado(a.getSaldoEsperado())
                .saldoContado(a.getSaldoContado())
                .diferencia(a.getDiferencia())
                .observaciones(a.getObservaciones())
                .realizadoPor(a.getRealizadoPor())
                .fechaCreacion(a.getFechaCreacion())
                .build();
    }
}
