package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.dto.command.ActualizarClientePerfilCommand;
import com.playzone.pems.application.usuario.dto.command.CompletarPerfilClienteCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClientePerfilCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClientePublicoCommand;
import com.playzone.pems.application.usuario.dto.query.ClientePerfilQuery;
import com.playzone.pems.application.usuario.port.in.ActualizarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.ActualizarSegmentoPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.CompletarPerfilClienteUseCase;
import com.playzone.pems.application.usuario.port.in.ActivarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.DesactivarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.HacerVipPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.ListarClientesPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.ObtenerClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.QuitarVipPerfilUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClientePerfilUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClientePublicoUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarVisitaPerfilUseCase;
import com.playzone.pems.application.usuario.port.out.SupabaseAuthPort;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.model.PerfilUsuario;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.domain.usuario.repository.PerfilUsuarioRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientePerfilService
        implements RegistrarClientePerfilUseCase,
        RegistrarClientePublicoUseCase,
        ActualizarClientePerfilUseCase,
        ListarClientesPerfilUseCase,
        ObtenerClientePerfilUseCase,
        ActivarClientePerfilUseCase,
        DesactivarClientePerfilUseCase,
        HacerVipPerfilUseCase,
        QuitarVipPerfilUseCase,
        RegistrarVisitaPerfilUseCase,
        ActualizarSegmentoPerfilUseCase,
        CompletarPerfilClienteUseCase {

    private final ClientePerfilRepository clientePerfilRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final SupabaseAuthPort        supabaseAuthPort;

    @Override
    @Transactional
    public ClientePerfil ejecutar(RegistrarClientePublicoCommand command) {
        // 1. Validaciones previas (Evitar llamadas innecesarias a Supabase)
        if (command.getTipoDocumentoCodigo() == null || command.getTipoDocumentoCodigo().isBlank()) {
            throw new ValidationException("tipoDocumento", "El tipo de documento es obligatorio.");
        }
        if (command.getNumeroDocumento() == null || command.getNumeroDocumento().isBlank()) {
            throw new ValidationException("numeroDocumento", "El número de documento es obligatorio.");
        }

        // 2. Validar si ya existe un cliente con ese correo
        Optional<ClientePerfil> clienteExistente = clientePerfilRepository.buscarPorCorreo(command.getCorreo());

        if (clienteExistente.isPresent()) {
            ClientePerfil cliente = clienteExistente.get();
            if (cliente.getUsuarioId() != null) {
                throw new ValidationException("correo", "El correo ya está registrado y vinculado a una cuenta.");
            }
            
            // Caso POS: El cliente existe pero no tiene usuarioId
            // 2. Crear usuario en Supabase
            UUID usuarioId = supabaseAuthPort.crearUsuario(command.getCorreo(), command.getPassword(), command.getNombre(), false);

            String fotoGoogle = perfilUsuarioRepository.buscarPorId(usuarioId)
                    .map(PerfilUsuario::getFotoPerfilPath)
                    .orElse(null);

            // 3. Vincular usuarioId al cliente existente
            ClientePerfil vinculado = cliente.toBuilder()
                    .usuarioId(usuarioId)
                    .nombres(command.getNombre()) // Actualizamos el nombre con el del registro web si es necesario
                    .origen("WEB")
                    .fotoPerfilPath(fotoGoogle)
                    .build();

            return clientePerfilRepository.guardar(vinculado);
        }

        // Caso Nuevo: No existe el cliente por correo
        // 2. Validar duplicado por documento
        clientePerfilRepository.buscarPorDocumento(
                command.getTipoDocumentoCodigo(), command.getNumeroDocumento()
        ).ifPresent(c -> {
            throw new ValidationException("numeroDocumento", "Ya existe una cuenta con ese documento.");
        });

        // 3. Crear usuario en Supabase
        UUID usuarioId = supabaseAuthPort.crearUsuario(command.getCorreo(), command.getPassword(), command.getNombre(), false);

        String fotoGoogle = perfilUsuarioRepository.buscarPorId(usuarioId)
                .map(PerfilUsuario::getFotoPerfilPath)
                .orElse(null);

        // 4. Crear cliente_perfil
        ClientePerfil nuevo = ClientePerfil.builder()
                .usuarioId(usuarioId)
                .tipoDocumentoCodigo(command.getTipoDocumentoCodigo())
                .numeroDocumento(command.getNumeroDocumento())
                .nombres(command.getNombre())
                .correo(command.getCorreo())
                .telefono(command.getTelefono())
                .origen("WEB")
                .aceptaComunicaciones(true)
                .segmentoCodigo("NUEVO")
                .esVip(false)
                .contadorVisitas(0)
                .totalGastado(BigDecimal.ZERO)
                .fotoPerfilPath(fotoGoogle)
                .build();

        return clientePerfilRepository.guardar(nuevo);
    }

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

        String rucVal = null;
        String razonSocialVal = null;
        if ("RUC".equalsIgnoreCase(command.getTipoDocumentoCodigo())) {
            rucVal = command.getNumeroDocumento();
            StringBuilder sb = new StringBuilder(command.getNombres() != null ? command.getNombres() : "");
            if (command.getApellidoPaterno() != null) {
                sb.append(" ").append(command.getApellidoPaterno());
            }
            if (command.getApellidoMaterno() != null) {
                sb.append(" ").append(command.getApellidoMaterno());
            }
            razonSocialVal = sb.toString().trim();
        }

        String fotoGoogle = (command.getUsuarioId() != null) 
                ? perfilUsuarioRepository.buscarPorId(command.getUsuarioId()).map(PerfilUsuario::getFotoPerfilPath).orElse(null) 
                : null;

        ClientePerfil nuevo = ClientePerfil.builder()
                .usuarioId(command.getUsuarioId())
                .tipoDocumentoCodigo(command.getTipoDocumentoCodigo())
                .numeroDocumento(command.getNumeroDocumento())
                .nombres(command.getNombres())
                .apellidoPaterno(command.getApellidoPaterno())
                .apellidoMaterno(command.getApellidoMaterno())
                .correo(command.getCorreo())
                .telefono(command.getTelefono())
                .ruc(rucVal)
                .razonSocial(razonSocialVal)
                .origen(command.getOrigen() != null ? command.getOrigen() : "ADMIN")
                .aceptaComunicaciones(command.isAceptaComunicaciones())
                .segmentoCodigo("NUEVO")
                .esVip(false)
                .contadorVisitas(0)
                .totalGastado(BigDecimal.ZERO)
                .fotoPerfilPath(fotoGoogle)
                .build();

        return clientePerfilRepository.guardar(nuevo);
    }

    @Override
    @Transactional
    public ClientePerfil ejecutar(CompletarPerfilClienteCommand command) {
        if (command.getUsuarioId() == null) {
            throw new ValidationException("usuarioId", "No autenticado.");
        }
        if (command.getNumeroDocumento() == null || command.getNumeroDocumento().isBlank()) {
            throw new ValidationException("numeroDocumento", "El número de documento es obligatorio.");
        }

        clientePerfilRepository.buscarPorUsuarioId(command.getUsuarioId()).ifPresent(c -> {
            throw new ValidationException("usuarioId", "Ya tienes un perfil registrado.");
        });

        Optional<ClientePerfil> porCorreo = command.getCorreo() != null
                ? clientePerfilRepository.buscarPorCorreo(command.getCorreo())
                : Optional.empty();

        String fotoGoogle = perfilUsuarioRepository.buscarPorId(command.getUsuarioId())
                .map(PerfilUsuario::getFotoPerfilPath)
                .orElse(null);

        if (porCorreo.isPresent()) {
            ClientePerfil existente = porCorreo.get();
            if (existente.getUsuarioId() != null) {
                throw new ValidationException("correo", "Ese correo ya está vinculado a otra cuenta.");
            }
            clientePerfilRepository.buscarPorDocumento(
                            command.getTipoDocumentoCodigo(), command.getNumeroDocumento())
                    .filter(d -> !d.getId().equals(existente.getId()))
                    .ifPresent(d -> {
                        throw new ValidationException("numeroDocumento", "Ya existe una cuenta con ese documento.");
                    });

            ClientePerfil vinculado = existente.toBuilder()
                    .usuarioId(command.getUsuarioId())
                    .tipoDocumentoCodigo(command.getTipoDocumentoCodigo())
                    .numeroDocumento(command.getNumeroDocumento())
                    .nombres(command.getNombres())
                    .apellidoPaterno(command.getApellidoPaterno())
                    .apellidoMaterno(command.getApellidoMaterno())
                    .telefono(command.getTelefono())
                    .origen("WEB")
                    .aceptaComunicaciones(command.isAceptaComunicaciones())
                    .fotoPerfilPath(fotoGoogle)
                    .build();
            return clientePerfilRepository.guardar(vinculado);
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
                .origen("WEB")
                .aceptaComunicaciones(command.isAceptaComunicaciones())
                .segmentoCodigo("NUEVO")
                .esVip(false)
                .contadorVisitas(0)
                .totalGastado(BigDecimal.ZERO)
                .fotoPerfilPath(fotoGoogle)
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
                .telefono(command.getTelefono() != null ? command.getTelefono() : existente.getTelefono())
                .correo(command.getCorreo() != null ? command.getCorreo() : existente.getCorreo())
                .aceptaComunicaciones(command.getAceptaComunicaciones() != null
                        ? command.getAceptaComunicaciones()
                        : existente.isAceptaComunicaciones())
                .fotoPerfilPath(command.isActualizarFotoPerfil()
                        ? command.getFotoPerfilPath()
                        : existente.getFotoPerfilPath())
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
