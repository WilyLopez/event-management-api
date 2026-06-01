package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.dto.command.ActualizarClienteCommand;
import com.playzone.pems.application.usuario.dto.command.MigrarClienteWebCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClienteAdminCommand;
import com.playzone.pems.application.usuario.dto.command.RegistrarClienteCommand;
import com.playzone.pems.application.usuario.dto.query.ClientePageQuery;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;
import com.playzone.pems.application.usuario.port.in.ActualizarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.ActivarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.ActualizarSegmentoClienteUseCase;
import com.playzone.pems.application.usuario.port.in.AutenticarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.DesactivarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.EliminarFotoClienteUseCase;
import com.playzone.pems.application.usuario.port.in.HacerVipUseCase;
import com.playzone.pems.application.usuario.port.in.ListarClientesUseCase;
import com.playzone.pems.application.usuario.port.in.MigrarClienteWebUseCase;
import com.playzone.pems.application.usuario.port.in.ObtenerClienteUseCase;
import com.playzone.pems.application.usuario.port.in.QuitarVipUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClienteAdminUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarClienteUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarVisitaManualUseCase;
import com.playzone.pems.application.usuario.port.in.SubirFotoClienteUseCase;
import com.playzone.pems.domain.storage.StoragePort;
import com.playzone.pems.application.usuario.port.in.AutenticarClienteUseCase.Command;
import com.playzone.pems.application.usuario.port.in.AutenticarClienteUseCase.Result;
import com.playzone.pems.application.usuario.port.out.EnviarCorreoVerificacionPort;
import com.playzone.pems.application.usuario.port.out.GenerarTokenPort;
import com.playzone.pems.domain.usuario.exception.ClienteNotFoundException;
import com.playzone.pems.domain.usuario.exception.UsuarioBlockedException;
import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.EncriptacionUtil;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClienteService
        implements RegistrarClienteUseCase,
        RegistrarClienteAdminUseCase,
        MigrarClienteWebUseCase,
        AutenticarClienteUseCase,
        ActualizarClienteUseCase,
        ListarClientesUseCase,
        ObtenerClienteUseCase,
        ActivarClienteUseCase,
        DesactivarClienteUseCase,
        HacerVipUseCase,
        QuitarVipUseCase,
        RegistrarVisitaManualUseCase,
        ActualizarSegmentoClienteUseCase,
        SubirFotoClienteUseCase,
        EliminarFotoClienteUseCase {

    private final ClienteRepository            clienteRepository;
    private final GenerarTokenPort             generarTokenPort;
    private final EnviarCorreoVerificacionPort correoPort;
    private final StoragePort                  storagePort;

    @Value("${playzone.seguridad.max-intentos-login:5}")
    private int maxIntentos;

    @Value("${playzone.seguridad.duracion-bloqueo-min:15}")
    private int duracionBloqueoMin;

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
                .tipoCliente(command.getRuc() != null ? "EMPRESA" : "PERSONA")
                .esVip(false)
                .contadorVisitas(0)
                .correoVerificado(true)
                .activo(true)
                .origenRegistro("WEB")
                .tieneAccesoWeb(true)
                .aceptaComunicaciones(true)
                .segmentoCliente("NUEVO")
                .totalGastado(BigDecimal.ZERO)
                .build();

        Cliente guardado = clienteRepository.save(cliente);
        correoPort.enviarBienvenida(guardado.getCorreo(), guardado.getNombre());
        return toQuery(guardado);
    }

    @Override
    @Transactional
    public ClienteQuery ejecutar(RegistrarClienteAdminCommand command) {
        if (command.getCorreo() != null && clienteRepository.existsByCorreo(command.getCorreo())) {
            throw new ValidationException("correo", "Ya existe una cuenta con ese correo.");
        }
        if (command.getDni() != null && clienteRepository.existsByDni(command.getDni())) {
            throw new ValidationException("dni", "Ya existe una cuenta con ese DNI.");
        }

        Cliente cliente = Cliente.builder()
                .nombre(command.getNombre())
                .correo(command.getCorreo())
                .telefono(command.getTelefono())
                .dni(command.getDni())
                .fechaNacimiento(command.getFechaNacimiento())
                .observaciones(command.getObservaciones())
                .tipoCliente(command.getTipoCliente() != null ? command.getTipoCliente() : "PERSONA")
                .aceptaComunicaciones(command.isAceptaComunicaciones())
                .origenRegistro("ADMIN")
                .tieneAccesoWeb(false)
                .correoVerificado(false)
                .activo(true)
                .esVip(false)
                .contadorVisitas(0)
                .segmentoCliente("NUEVO")
                .totalGastado(BigDecimal.ZERO)
                .build();

        return toQuery(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteQuery ejecutar(MigrarClienteWebCommand command) {
        return clienteRepository.findByCorreo(command.getCorreo())
                .map(existente -> {
                    if (existente.isTieneAccesoWeb()) {
                        throw new ValidationException("correo", "Ya existe una cuenta registrada con este correo.");
                    }
                    return toQuery(clienteRepository.save(existente.toBuilder()
                            .nombre(command.getNombre())
                            .telefono(command.getTelefono())
                            .contrasenaHash(EncriptacionUtil.hashear(command.getContrasena()))
                            .tieneAccesoWeb(true)
                            .correoVerificado(true)
                            .fechaMigracionWeb(Instant.now())
                            .activo(true)
                            .build()));
                })
                .orElseGet(() -> {
                    Cliente nuevo = Cliente.builder()
                            .nombre(command.getNombre())
                            .correo(command.getCorreo())
                            .contrasenaHash(EncriptacionUtil.hashear(command.getContrasena()))
                            .telefono(command.getTelefono())
                            .tipoCliente("PERSONA")
                            .origenRegistro("WEB")
                            .tieneAccesoWeb(true)
                            .correoVerificado(true)
                            .aceptaComunicaciones(true)
                            .activo(true)
                            .esVip(false)
                            .contadorVisitas(0)
                            .segmentoCliente("NUEVO")
                            .totalGastado(BigDecimal.ZERO)
                            .build();
                    Cliente guardado = clienteRepository.save(nuevo);
                    correoPort.enviarBienvenida(guardado.getCorreo(), guardado.getNombre());
                    return toQuery(guardado);
                });
    }

    @Override
    @Transactional
    public Result ejecutar(Command command) {
        LocalDateTime ahora = FechaUtil.ahoraPeru();

        Cliente cliente = clienteRepository.findByCorreo(command.correo())
                .orElseThrow(() -> new ValidationException("Credenciales incorrectas."));

        if (cliente.estaBloqueado(ahora)) {
            throw new UsuarioBlockedException(cliente.getBloqueadoHasta());
        }

        if (!cliente.puedeAcceder()) {
            throw new ValidationException(
                    "La cuenta no está activa o el correo no fue verificado.");
        }

        if (!EncriptacionUtil.verificar(command.contrasena(), cliente.getContrasenaHash())) {
            clienteRepository.incrementarIntentosFallidos(cliente.getId());
            if (cliente.getIntentosFallidos() + 1 >= maxIntentos) {
                clienteRepository.save(cliente.toBuilder()
                        .bloqueadoHasta(ahora.plusMinutes(duracionBloqueoMin))
                        .build());
            }
            throw new ValidationException("Credenciales incorrectas.");
        }

        clienteRepository.reiniciarIntentosFallidos(cliente.getId());

        String token = generarTokenPort.generarTokenAcceso(
                cliente.getId(), cliente.getCorreo(), "CLIENTE");

        return new Result(token, cliente.getId(), cliente.getNombre());
    }

    @Override
    @Transactional
    public ClienteQuery ejecutar(Long idCliente, ActualizarClienteCommand command) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNotFoundException(idCliente));

        boolean aceptaCom = command.getAceptaComunicaciones() != null
                ? command.getAceptaComunicaciones()
                : cliente.isAceptaComunicaciones();

        return toQuery(clienteRepository.save(cliente.toBuilder()
                .nombre(command.getNombre())
                .telefono(command.getTelefono())
                .ruc(command.getRuc())
                .razonSocial(command.getRazonSocial())
                .direccionFiscal(command.getDireccionFiscal())
                .aceptaComunicaciones(aceptaCom)
                .build()));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePageQuery ejecutar(
            String search,
            Boolean esVip,
            Boolean activo,
            Boolean verificado,
            Boolean frecuente,
            Pageable pageable) {

        return ejecutar(search, esVip, activo, verificado, frecuente,
                null, null, null, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePageQuery ejecutar(
            String search,
            Boolean esVip,
            Boolean activo,
            Boolean verificado,
            Boolean frecuente,
            Boolean tieneAccesoWeb,
            Boolean aceptaComunicaciones,
            String origenRegistro,
            String segmentoCliente,
            Pageable pageable) {

        final int MIN_VISITAS_FRECUENTE = 5;

        Page<Cliente> pagina = clienteRepository.buscarConFiltrosCrm(
                search, esVip, activo, verificado, frecuente,
                tieneAccesoWeb, aceptaComunicaciones,
                origenRegistro, segmentoCliente,
                MIN_VISITAS_FRECUENTE, pageable);

        return ClientePageQuery.builder()
                .content(pagina.getContent().stream().map(this::toQuery).toList())
                .page(pagina.getNumber())
                .size(pagina.getSize())
                .totalElements(pagina.getTotalElements())
                .totalPages(pagina.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteQuery ejecutar(Long id) {
        return toQuery(clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id)));
    }

    @Override
    @Transactional
    public void activar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        if (!cliente.isActivo()) {
            clienteRepository.save(cliente.toBuilder().activo(true).build());
        }
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        if (cliente.isActivo()) {
            clienteRepository.save(cliente.toBuilder().activo(false).build());
        }
    }

    @Override
    @Transactional
    public ClienteQuery ejecutar(Long id, int descuentoPorcentaje) {
        if (descuentoPorcentaje < 0 || descuentoPorcentaje > 100) {
            throw new ValidationException("descuento", "El descuento debe estar entre 0 y 100.");
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        return toQuery(clienteRepository.save(cliente.toBuilder()
                .esVip(true)
                .descuentoVip(BigDecimal.valueOf(descuentoPorcentaje))
                .build()));
    }

    @Override
    @Transactional
    public ClienteQuery quitarVip(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        return toQuery(clienteRepository.save(cliente.toBuilder()
                .esVip(false)
                .descuentoVip(null)
                .build()));
    }

    @Override
    @Transactional
    public void ejecutarVisita(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNotFoundException(idCliente));

        clienteRepository.save(cliente.toBuilder()
                .contadorVisitas(cliente.getContadorVisitas() + 1)
                .build());
    }

    @Override
    @Transactional
    public void ejecutar(Long idCliente, String segmento) {
        if (!clienteRepository.findById(idCliente).isPresent()) {
            throw new ClienteNotFoundException(idCliente);
        }
        clienteRepository.actualizarSegmento(idCliente, segmento);
    }

    @Override
    @Transactional
    public ClienteQuery ejecutar(Long idCliente, MultipartFile foto) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNotFoundException(idCliente));

        if (cliente.getFotoPerfil() != null) {
            storagePort.eliminar(cliente.getFotoPerfil());
        }

        String ruta       = storagePort.guardar(foto, "perfiles");
        String urlPublica = storagePort.obtenerUrlPublica(ruta);
        return toQuery(clienteRepository.save(cliente.toBuilder().fotoPerfil(urlPublica).build()));
    }

    @Override
    @Transactional
    public ClienteQuery eliminarFoto(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNotFoundException(idCliente));

        if (cliente.getFotoPerfil() != null) {
            storagePort.eliminar(cliente.getFotoPerfil());
        }

        return toQuery(clienteRepository.save(cliente.toBuilder().fotoPerfil(null).build()));
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
                .direccionFiscal(c.getDireccionFiscal())
                .fotoPerfil(c.getFotoPerfil())
                .ultimoLogin(c.getUltimoLogin())
                .fechaNacimiento(c.getFechaNacimiento())
                .tipoCliente(c.getTipoCliente())
                .esVip(c.isEsVip())
                .descuentoVip(c.getDescuentoVip())
                .contadorVisitas(c.getContadorVisitas())
                .correoVerificado(c.isCorreoVerificado())
                .activo(c.isActivo())
                .origenRegistro(c.getOrigenRegistro())
                .tieneAccesoWeb(c.isTieneAccesoWeb())
                .aceptaComunicaciones(c.isAceptaComunicaciones())
                .observaciones(c.getObservaciones())
                .fechaMigracionWeb(c.getFechaMigracionWeb())
                .ultimaVisita(c.getUltimaVisita())
                .totalGastado(c.getTotalGastado())
                .segmentoCliente(c.getSegmentoCliente())
                .fechaCreacion(c.getFechaCreacion())
                .build();
    }
}