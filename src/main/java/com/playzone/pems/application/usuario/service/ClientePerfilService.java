package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.dto.command.ActualizarClientePerfilCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClientePerfilCommand;
import com.playzone.pems.application.usuario.dto.query.ClientePerfilQuery;
import com.playzone.pems.application.usuario.port.in.ActualizarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.ActualizarSegmentoPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.ActivarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.DesactivarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.HacerVipPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.ListarClientesPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.ObtenerClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.QuitarVipPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarVisitaPerfilUseCase;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ClientePerfilService
        implements RegistrarClientePerfilUseCase,
        ActualizarClientePerfilUseCase,
        ListarClientesPerfilUseCase,
        ObtenerClientePerfilUseCase,
        ActivarClientePerfilUseCase,
        DesactivarClientePerfilUseCase,
        HacerVipPerfilUseCase,
        QuitarVipPerfilUseCase,
        RegistrarVisitaPerfilUseCase,
        ActualizarSegmentoPerfilUseCase {

    private final ClientePerfilRepository clientePerfilRepository;

    @Override
    @Transactional
    public ClientePerfil ejecutar(RegistrarClientePerfilCommand command) {
        if (command.getCorreo() != null) {
            clientePerfilRepository.buscarPorCorreo(command.getCorreo()).ifPresent(c -> {
                throw new ValidationException("correo", "Ya existe una cuenta con ese correo.");
            });
        }
        clientePerfilRepository.buscarPorDocumento(
                command.getTipoDocumentoCodigo(), command.getNumeroDocumento()
        ).ifPresent(c -> {
            throw new ValidationException("numeroDocumento", "Ya existe una cuenta con ese documento.");
        });

        ClientePerfil nuevo = ClientePerfil.builder()
                .usuarioId(command.getUsuarioId())
                .tipoDocumentoCodigo(command.getTipoDocumentoCodigo())
                .numeroDocumento(command.getNumeroDocumento())
                .nombres(command.getNombres())
                .apellidoPaterno(command.getApellidoPaterno())
                .apellidoMaterno(command.getApellidoMaterno())
                .correo(command.getCorreo())
                .telefono(command.getTelefono())
                .origen(command.getOrigen() != null ? command.getOrigen() : "ADMIN")
                .aceptaComunicaciones(command.isAceptaComunicaciones())
                .segmentoCodigo("NUEVO")
                .esVip(false)
                .contadorVisitas(0)
                .totalGastado(BigDecimal.ZERO)
                .build();

        return clientePerfilRepository.guardar(nuevo);
    }

    @Override
    @Transactional
    public ClientePerfil ejecutar(Long id, ActualizarClientePerfilCommand command) {
        ClientePerfil existente = clientePerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", id));

        ClientePerfil actualizado = existente.toBuilder()
                .nombres(command.getNombres() != null ? command.getNombres() : existente.getNombres())
                .apellidoPaterno(command.getApellidoPaterno() != null ? command.getApellidoPaterno() : existente.getApellidoPaterno())
                .apellidoMaterno(command.getApellidoMaterno() != null ? command.getApellidoMaterno() : existente.getApellidoMaterno())
                .telefono(command.getTelefono())
                .correo(command.getCorreo() != null ? command.getCorreo() : existente.getCorreo())
                .aceptaComunicaciones(command.getAceptaComunicaciones() != null
                        ? command.getAceptaComunicaciones()
                        : existente.isAceptaComunicaciones())
                .build();

        return clientePerfilRepository.guardar(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientePerfil> ejecutar(ClientePerfilQuery query, Pageable pageable) {
        return clientePerfilRepository.listarPaginado(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePerfil ejecutar(Long id) {
        return clientePerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", id));
    }

    @Override
    @Transactional
    public void activar(Long id) {
        clientePerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", id));
        clientePerfilRepository.reactivar(id);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        clientePerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", id));
        clientePerfilRepository.desactivar(id);
    }

    @Override
    @Transactional
    public ClientePerfil ejecutar(Long id, BigDecimal descuento) {
        if (descuento == null || descuento.compareTo(BigDecimal.ZERO) < 0 || descuento.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidationException("descuento", "El descuento debe estar entre 0 y 100.");
        }
        clientePerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", id));
        clientePerfilRepository.marcarComoVip(id, descuento);
        return clientePerfilRepository.buscarPorId(id).orElseThrow();
    }

    @Override
    @Transactional
    public ClientePerfil quitarVip(Long id) {
        clientePerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", id));
        clientePerfilRepository.quitarVip(id);
        return clientePerfilRepository.buscarPorId(id).orElseThrow();
    }

    @Override
    @Transactional
    public void ejecutarVisita(Long idCliente) {
        clientePerfilRepository.buscarPorId(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", idCliente));
        clientePerfilRepository.incrementarContadorVisitas(idCliente);
        clientePerfilRepository.actualizarUltimaVisita(idCliente, OffsetDateTime.now());
    }

    @Override
    @Transactional
    public void ejecutar(Long id, String segmentoCodigo) {
        clientePerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientePerfil", id));
        clientePerfilRepository.actualizarSegmento(id, segmentoCodigo);
    }
}
