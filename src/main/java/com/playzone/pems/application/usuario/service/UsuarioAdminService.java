package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.port.in.AutenticarAdminUseCase;
import com.playzone.pems.application.usuario.port.in.GestionarUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.out.GenerarTokenPort;
import com.playzone.pems.domain.usuario.exception.UsuarioBlockedException;
import com.playzone.pems.domain.usuario.model.UsuarioAdmin;
import com.playzone.pems.domain.usuario.repository.UsuarioAdminRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.EncriptacionUtil;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioAdminService
        implements AutenticarAdminUseCase,
        GestionarUsuarioAdminUseCase {

    private final UsuarioAdminRepository usuarioAdminRepository;
    private final GenerarTokenPort       generarTokenPort;

    @Value("${playzone.seguridad.max-intentos-login:5}")
    private int maxIntentos;

    @Value("${playzone.seguridad.duracion-bloqueo-min:15}")
    private int duracionBloqueoMin;

    @Override
    @Transactional
    public Result ejecutar(Command command) {
        LocalDateTime ahora = FechaUtil.ahoraPeru();

        UsuarioAdmin admin = usuarioAdminRepository.findByCorreo(command.correo())
                .orElseThrow(() -> new ValidationException("Credenciales incorrectas."));

        if (admin.estaBloqueado(ahora)) {
            throw new UsuarioBlockedException(admin.getBloqueadoHasta());
        }

        if (!EncriptacionUtil.verificar(command.contrasena(), admin.getContrasenaHash())) {
            usuarioAdminRepository.incrementarIntentosFallidos(admin.getId());

            if (admin.getIntentosFallidos() + 1 >= maxIntentos) {
                UsuarioAdmin bloqueado = admin.toBuilder()
                        .bloqueadoHasta(ahora.plusMinutes(duracionBloqueoMin))
                        .build();
                usuarioAdminRepository.save(bloqueado);
            }
            throw new ValidationException("Credenciales incorrectas.");
        }

        usuarioAdminRepository.reiniciarIntentosFallidos(admin.getId());

        String token = generarTokenPort.generarTokenAcceso(
                admin.getId(), admin.getCorreo(), "ADMIN");

        return new Result(token, admin.getId(), admin.getNombre(), admin.getIdSede());
    }

    @Override
    public List<UsuarioAdmin> listar() {
        return usuarioAdminRepository.findAll();
    }

    @Override
    public UsuarioAdmin obtener(Long idAdmin) {
        return usuarioAdminRepository.findById(idAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", idAdmin));
    }

    @Override
    @Transactional
    public UsuarioAdmin crear(CrearCommand command) {
        if (usuarioAdminRepository.existsByCorreo(command.correo())) {
            throw new ValidationException("correo", "Ya existe un administrador con ese correo.");
        }

        UsuarioAdmin nuevo = UsuarioAdmin.builder()
                .idSede(command.idSede())
                .nombre(command.nombre())
                .correo(command.correo())
                .contrasenaHash(EncriptacionUtil.hashear(command.contrasena()))
                .rol(command.rol() != null ? command.rol() : "ADMINISTRATIVO")
                .telefono(command.telefono())
                .activo(true)
                .debeCambiarContrasena(true)
                .intentosFallidos(0)
                .build();

        return usuarioAdminRepository.save(nuevo);
    }

    @Override
    @Transactional
    public UsuarioAdmin actualizarPerfil(Long idAdmin, ActualizarPerfilCommand command) {
        UsuarioAdmin admin = usuarioAdminRepository.findById(idAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", idAdmin));

        UsuarioAdmin actualizado = admin.toBuilder()
                .nombre(command.nombre() != null ? command.nombre() : admin.getNombre())
                .telefono(command.telefono())
                .build();

        return usuarioAdminRepository.save(actualizado);
    }

    @Override
    @Transactional
    public void cambiarContrasena(Long idAdmin, CambiarContrasenaCommand command) {
        UsuarioAdmin admin = usuarioAdminRepository.findById(idAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", idAdmin));

        if (!EncriptacionUtil.verificar(command.contrasenaActual(), admin.getContrasenaHash())) {
            throw new ValidationException("contrasenaActual", "La contraseña actual es incorrecta.");
        }

        usuarioAdminRepository.save(admin.toBuilder()
                .contrasenaHash(EncriptacionUtil.hashear(command.contrasenaNueva()))
                .debeCambiarContrasena(false)
                .ultimoCambioContrasena(FechaUtil.ahoraPeru())
                .build());
    }

    @Override
    @Transactional
    public void desactivar(Long idAdmin) {
        UsuarioAdmin admin = usuarioAdminRepository.findById(idAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", idAdmin));

        usuarioAdminRepository.save(admin.toBuilder().activo(false).build());
    }

    @Override
    @Transactional
    public void activar(Long idAdmin) {
        UsuarioAdmin admin = usuarioAdminRepository.findById(idAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", idAdmin));

        usuarioAdminRepository.save(admin.toBuilder().activo(true).build());
    }
}
