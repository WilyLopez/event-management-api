package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.dto.command.ActualizarClienteCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClienteCommand;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;
import com.playzone.pems.application.usuario.port.in.ActualizarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.AutenticarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClienteUseCase;
import com.playzone.pems.application.usuario.port.out.EnviarCorreoVerificacionPort;
import com.playzone.pems.application.usuario.port.out.GenerarTokenPort;
import com.playzone.pems.domain.usuario.exception.ClienteNotFoundException;
import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.EncriptacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService
        implements RegistrarClienteUseCase,
        AutenticarClienteUseCase,
        ActualizarClienteUseCase {

    private final ClienteRepository            clienteRepository;
    private final GenerarTokenPort             generarTokenPort;
    private final EnviarCorreoVerificacionPort correoPort;

    @Override
    @Transactional
    public ClienteQuery ejecutar(RegistrarClienteCommand command) {
        if (clienteRepository.existsByCorreo(command.getCorreo())) {
            throw new ValidationException("correo", "Ya existe una cuenta con ese correo.");
        }
        if (command.getDni() != null && clienteRepository.existsByDni(command.getDni())) {
            throw new ValidationException("dni", "Ya existe una cuenta con ese DNI.");
        }

        Cliente cliente = Cliente.builder()
                .nombre(command.getNombre())
                .correo(command.getCorreo())
                .contrasenaHash(EncriptacionUtil.hashear(command.getContrasena()))
                .telefono(command.getTelefono())
                .dni(command.getDni())
                .ruc(command.getRuc())
                .razonSocial(command.getRazonSocial())
                .direccionFiscal(command.getDireccionFiscal())
                .esVip(false)
                .contadorVisitas(0)
                .correoVerificado(true)
                .activo(true)
                .build();

        Cliente guardado = clienteRepository.save(cliente);

        correoPort.enviarBienvenida(guardado.getCorreo(), guardado.getNombre());

        return toQuery(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Result ejecutar(Command command) {
        Cliente cliente = clienteRepository.findByCorreo(command.correo())
                .orElseThrow(() -> new ValidationException("Credenciales incorrectas."));

        if (!cliente.puedeAcceder()) {
            throw new ValidationException("La cuenta no está activa o el correo no fue verificado.");
        }
        if (!EncriptacionUtil.verificar(command.contrasena(), cliente.getContrasenaHash())) {
            throw new ValidationException("Credenciales incorrectas.");
        }

        String token = generarTokenPort.generarTokenAcceso(
                cliente.getId(), cliente.getCorreo(), "CLIENTE");

        return new Result(token, cliente.getId(), cliente.getNombre());
    }

    @Override
    @Transactional
    public ClienteQuery ejecutar(Long idCliente, ActualizarClienteCommand command) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNotFoundException(idCliente));

        Cliente actualizado = cliente.toBuilder()
                .nombre(command.getNombre())
                .telefono(command.getTelefono())
                .ruc(command.getRuc())
                .razonSocial(command.getRazonSocial())
                .direccionFiscal(command.getDireccionFiscal())
                .build();

        return toQuery(clienteRepository.save(actualizado));
    }

    private ClienteQuery toQuery(Cliente c) {
        return ClienteQuery.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .correo(c.getCorreo())
                .telefono(c.getTelefono())
                .dni(c.getDni())
                .ruc(c.getRuc())
                .razonSocial(c.getRazonSocial())
                .esVip(c.isEsVip())
                .contadorVisitas(c.getContadorVisitas())
                .correoVerificado(c.isCorreoVerificado())
                .activo(c.isActivo())
                .fechaCreacion(c.getFechaCreacion())
                .build();
    }
}