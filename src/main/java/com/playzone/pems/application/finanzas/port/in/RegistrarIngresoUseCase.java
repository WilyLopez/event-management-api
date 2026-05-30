package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.command.RegistrarIngresoManualCommand;
import com.playzone.pems.application.finanzas.dto.query.RegistroIngresoQuery;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RegistrarIngresoUseCase {
    RegistroIngresoQuery registrar(RegistrarIngresoManualCommand command);
    void registrarAutomatico(CategoriaIngreso categoria, Long idSede, Long idReservaPublica,
                             Long idEventoPrivado, BigDecimal monto, LocalDate fecha, String medioPago);
    Page<RegistroIngresoQuery> listar(Long idSede, Pageable pageable);
    List<RegistroIngresoQuery> listarPorRango(Long idSede, LocalDate inicio, LocalDate fin);
    void eliminar(Long id);
}
