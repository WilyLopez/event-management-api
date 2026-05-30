package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.command.ActualizarEgresoCommand;
import com.playzone.pems.application.finanzas.dto.command.RegistrarEgresoCommand;
import com.playzone.pems.application.finanzas.dto.query.RegistroEgresoQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RegistrarEgresoUseCase {
    RegistroEgresoQuery registrar(RegistrarEgresoCommand command);
    RegistroEgresoQuery actualizar(ActualizarEgresoCommand command);
    Page<RegistroEgresoQuery> listar(Long idSede, Pageable pageable);
    List<RegistroEgresoQuery> listarPorPeriodo(Long idSede, int anio, int mes);
    List<RegistroEgresoQuery> listarPorRango(Long idSede, LocalDate inicio, LocalDate fin);
    void eliminar(Long id);
}
