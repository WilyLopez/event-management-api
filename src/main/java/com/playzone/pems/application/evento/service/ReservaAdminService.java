package com.playzone.pems.application.evento.service;

import com.playzone.pems.application.evento.dto.query.MetricasReservaQuery;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.in.BuscarReservasAdminUseCase;
import com.playzone.pems.application.evento.port.in.ConfirmarIngresoUseCase;
import com.playzone.pems.domain.evento.exception.ReservaNotFoundException;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ReservaAdminService
        implements ConfirmarIngresoUseCase,
                   BuscarReservasAdminUseCase {

    private final ReservaPublicaRepository reservaRepository;
    private final ClienteRepository        clienteRepository;

    @Override
    @Transactional
    public ReservaPublicaQuery ejecutar(Long idReserva, Long idUsuarioAdmin) {
        ReservaPublica reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (!reserva.puedeRegistrarIngreso()) {
            throw new ValidationException(
                    "La reserva ya fue ingresada o su estado no permite registrar ingreso.");
        }

        ReservaPublica actualizada = reserva.toBuilder()
                .estado(EstadoReservaPublica.COMPLETADA)
                .ingresado(true)
                .fechaIngreso(LocalDateTime.now(ZoneId.of("America/Lima")))
                .build();

        ReservaPublica guardada = reservaRepository.save(actualizada);
        String nombre = clienteRepository.findById(guardada.getIdCliente())
                .map(Cliente::getNombre).orElse(null);

        return toQuery(guardada, nombre, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaPublicaQuery> buscar(
            Long idSede, String estado, LocalDate fecha,
            Boolean ingresado, Boolean esReprogramacion,
            String search, Pageable pageable) {

        EstadoReservaPublica estadoEnum = null;
        if (estado != null && !estado.isBlank()) {
            try { estadoEnum = EstadoReservaPublica.valueOf(estado); }
            catch (IllegalArgumentException ignored) {}
        }

        String searchPattern = (search != null && !search.isBlank()) 
                ? "%" + search.toLowerCase() + "%" : null;

        return reservaRepository.buscarAdmin(
                idSede, estadoEnum, fecha, ingresado, esReprogramacion,
                searchPattern,
                pageable
        ).map(r -> {
            String nombre = clienteRepository.findById(r.getIdCliente())
                    .map(Cliente::getNombre).orElse(null);
            String correo = clienteRepository.findById(r.getIdCliente())
                    .map(Cliente::getCorreo).orElse(null);
            return toQuery(r, nombre, correo);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public MetricasReservaQuery metricas(Long idSede, LocalDate fecha) {
        LocalDate dia = fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Lima"));
        return reservaRepository.calcularMetricas(idSede, dia);
    }

    private ReservaPublicaQuery toQuery(ReservaPublica r, String nombreCliente, String correoCliente) {
        return ReservaPublicaQuery.builder()
                .id(r.getId())
                .idCliente(r.getIdCliente())
                .nombreCliente(nombreCliente)
                .correoCliente(correoCliente)
                .idSede(r.getIdSede())
                .estado(r.getEstado().getCodigo())
                .canalReserva(r.getCanalReserva() != null ? r.getCanalReserva().getCodigo() : null)
                .tipoDia(r.getTipoDia() != null ? r.getTipoDia().getCodigo() : null)
                .fechaEvento(r.getFechaEvento())
                .numeroTicket(r.getNumeroTicket())
                .precioHistorico(r.getPrecioHistorico())
                .descuentoAplicado(r.getDescuentoAplicado())
                .totalPagado(r.getTotalPagado())
                .nombreNino(r.getNombreNino())
                .edadNino(r.getEdadNino())
                .nombreAcompanante(r.getNombreAcompanante())
                .dniAcompanante(r.getDniAcompanante())
                .firmoConsentimiento(r.isFirmoConsentimiento())
                .esReprogramacion(r.isEsReprogramacion())
                .vecesReprogramada(r.getVecesReprogramada())
                .ingresado(r.isIngresado())
                .fechaIngreso(r.getFechaIngreso())
                .codigoQr(r.getCodigoQr())
                .medioPago(r.getMedioPago())
                .referenciaPago(r.getReferenciaPago())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}