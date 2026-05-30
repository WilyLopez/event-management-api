package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.command.AbrirCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.CerrarCajaCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarMovimientoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.AperturaCajaQuery;
import com.playzone.pems.application.finanzas.dto.query.MovimientoCajaQuery;

import java.time.LocalDate;
import java.util.List;

public interface GestionarCajaUseCase {
    AperturaCajaQuery abrir(AbrirCajaCommand command);
    AperturaCajaQuery cerrar(CerrarCajaCommand command);
    AperturaCajaQuery obtenerPorSedeYFecha(Long idSede, LocalDate fecha);
    List<AperturaCajaQuery> listarPorRango(Long idSede, LocalDate inicio, LocalDate fin);
    MovimientoCajaQuery registrarMovimiento(RegistrarMovimientoManualCommand command);
    List<MovimientoCajaQuery> listarMovimientos(Long idAperturaCaja);
    void eliminarMovimiento(Long idMovimiento);
}
