package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.usuario.dto.command.RegistrarUsuarioAdminCommand;
import com.playzone.pems.application.usuario.dto.response.UsuarioAdminResponse;
import com.playzone.pems.application.usuario.port.in.ActivarUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.in.ActualizarUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.in.CambiarRolUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.in.DesactivarUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.in.DesbloquearUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.in.ListarUsuariosAdminUseCase;
import com.playzone.pems.application.usuario.port.in.ObtenerUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.in.RegistrarUsuarioAdminUseCase;
import com.playzone.pems.application.usuario.port.in.ResetPasswordAdminUseCase;
import com.playzone.pems.application.usuario.port.out.EnviarCorreoBienvenidaPort;
import com.playzone.pems.application.usuario.port.out.SupabaseAuthPort;
import com.playzone.pems.domain.usuario.model.PerfilUsuario;
import com.playzone.pems.domain.usuario.model.Sede;
import com.playzone.pems.domain.usuario.model.StaffPerfil;
import com.playzone.pems.domain.usuario.model.UsuarioRol;
import com.playzone.pems.domain.usuario.repository.PerfilUsuarioRepository;
import com.playzone.pems.domain.usuario.repository.SedeRepository;
import com.playzone.pems.domain.usuario.repository.StaffPerfilRepository;
import com.playzone.pems.domain.usuario.repository.UsuarioRolRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService implements
        ListarUsuariosAdminUseCase,
        ObtenerUsuarioAdminUseCase,
        RegistrarUsuarioAdminUseCase,
        ActualizarUsuarioAdminUseCase,
        CambiarRolUsuarioAdminUseCase,
        ResetPasswordAdminUseCase,
        ActivarUsuarioAdminUseCase,
        DesactivarUsuarioAdminUseCase,
        DesbloquearUsuarioAdminUseCase {

    private static final int MAX_ADMINS   = 4;
    private static final int MAX_CAJEROS  = 5;
    private static final String SUPERADMIN = "SUPERADMIN";
    private static final String ADMIN      = "ADMIN";
    private static final String CAJERO     = "CAJERO";

    private static final String PW_UPPER   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String PW_LOWER   = "abcdefghijklmnopqrstuvwxyz";
    private static final String PW_DIGITS  = "0123456789";
    private static final String PW_SPECIAL = "!@#$%&*?";
    private static final String PW_ALL     = PW_UPPER + PW_LOWER + PW_DIGITS + PW_SPECIAL;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StaffPerfilRepository    staffPerfilRepository;
    private final PerfilUsuarioRepository  perfilUsuarioRepository;
    private final SedeRepository           sedeRepository;
    private final UsuarioRolRepository     usuarioRolRepository;
    private final SupabaseAuthPort         supabaseAuthPort;
    private final EnviarCorreoBienvenidaPort enviarCorreoBienvenidaPort;
    private final SupabaseAuthFacade       authFacade;
    private final RegistrarLogUseCase      auditoria;


    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAdminResponse> ejecutar() {
        List<StaffPerfil> staffList = staffPerfilRepository.listarTodos();
        List<UsuarioAdminResponse> response = new ArrayList<>();

        for (StaffPerfil staff : staffList) {
            PerfilUsuario perfil = perfilUsuarioRepository.buscarPorId(staff.getUsuarioId()).orElse(null);
            if (perfil == null) continue;
            Sede sede = sedeRepository.findById(staff.getSedeId()).orElse(null);
            List<String> roles = usuarioRolRepository.listarCodigosRolPorUsuario(staff.getUsuarioId());
            response.add(buildResponse(staff, perfil, sede, roles, null));
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioAdminResponse ejecutar(Long id) {
        StaffPerfil staff = staffPerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerfil", id));
        PerfilUsuario perfil = perfilUsuarioRepository.buscarPorId(staff.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("PerfilUsuario", "usuarioId", staff.getUsuarioId()));
        Sede sede = sedeRepository.findById(staff.getSedeId()).orElse(null);
        List<String> roles = usuarioRolRepository.listarCodigosRolPorUsuario(staff.getUsuarioId());
        return buildResponse(staff, perfil, sede, roles, null);
    }


    @Override
    @Transactional
    public UsuarioAdminResponse ejecutar(RegistrarUsuarioAdminCommand command) {
        String rol = command.getRolCodigo() != null ? command.getRolCodigo().toUpperCase() : "";

        if (!List.of(ADMIN, CAJERO).contains(rol)) {
            throw new ValidationException("rolCodigo", "Solo se permite crear usuarios con rol ADMIN o CAJERO.");
        }

        if (ADMIN.equals(rol)) {
            UUID actorId = authFacade.usuarioActualId().orElse(null);
            if (actorId == null) {
                throw new ValidationException("auth", "No se pudo identificar al solicitante.");
            }
            List<String> rolesActor = usuarioRolRepository.listarCodigosRolPorUsuario(actorId);
            if (!rolesActor.contains(SUPERADMIN)) {
                throw new ValidationException("rolCodigo", "Solo un SUPERADMIN puede crear usuarios con rol ADMIN.");
            }
        }

        validarLimiteRol(rol, -1L);

        String correoNorm = command.getCorreo().toLowerCase(java.util.Locale.ROOT).trim();

        perfilUsuarioRepository.buscarPorCorreo(correoNorm).ifPresent(u -> {
            throw new ValidationException("correo", "El correo ya se encuentra registrado.");
        });

        Sede sede = sedeRepository.findById(command.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", command.getSedeId()));

        String passwordFinal;
        if (command.isGenerarPassword()) {
            passwordFinal = generarPasswordTemporal();
        } else {
            validarFortalezaPassword(command.getPassword());
            passwordFinal = command.getPassword();
        }

        UUID usuarioId;
        try {
            usuarioId = supabaseAuthPort.crearUsuario(correoNorm, passwordFinal, command.getNombre());
        } catch (Exception ex) {
            throw new ValidationException("correo", "El correo ya se encuentra registrado en el sistema.");
        }

        usuarioRolRepository.eliminar(usuarioId, "CLIENTE");
        usuarioRolRepository.guardar(UsuarioRol.builder()
                .usuarioId(usuarioId)
                .rolCodigo(rol)
                .build());

        StaffPerfil staff = staffPerfilRepository.guardar(StaffPerfil.builder()
                .usuarioId(usuarioId)
                .sedeId(command.getSedeId())
                .esActivo(true)
                .debeCambiarContrasena(true)
                .intentosFallidos(0)
                .build());

        final String nombre     = command.getNombre();
        final String password   = passwordFinal;
        final String rolLabel   = ADMIN.equals(rol) ? "Administrador" : "Cajero";
        final String sedeNombre = sede.getNombre();
        CompletableFuture.runAsync(() -> {
            try {
                enviarCorreoBienvenidaPort.enviarCredencialesUsuario(correoNorm, nombre, password, rolLabel, sedeNombre);
            } catch (Exception ex) {
                log.warn("No se pudo enviar el correo de bienvenida a {}: {}", correoNorm, ex.getMessage());
            }
        });

        UUID actor = authFacade.usuarioActualId().orElse(null);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                actor, AuditoriaConstants.ACCION_CREAR, AuditoriaConstants.MOD_USUARIOS,
                "Staff", staff.getId(),
                null, correoNorm + " | rol=" + rol,
                "Usuario creado: " + nombre + " (" + correoNorm + ")",
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));

        String passwordTemporal = command.isGenerarPassword() ? passwordFinal : null;

        return UsuarioAdminResponse.builder()
                .id(staff.getId())
                .usuarioId(usuarioId)
                .nombre(nombre)
                .correo(correoNorm)
                .rol(rol)
                .idSede(command.getSedeId())
                .sedeNombre(sede.getNombre())
                .activo(true)
                .debeCambiarContrasena(true)
                .intentosFallidos(0)
                .passwordTemporal(passwordTemporal)
                .build();
    }


    @Override
    @Transactional
    public UsuarioAdminResponse ejecutar(Long id, String nombre, String telefono) {
        StaffPerfil staff = staffPerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerfil", id));

        perfilUsuarioRepository.actualizarPerfil(staff.getUsuarioId(), nombre, telefono);

        UUID actor = authFacade.usuarioActualId().orElse(null);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                actor, AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_USUARIOS,
                "Staff", id,
                null, "nombre=" + nombre,
                "Perfil actualizado para staff #" + id,
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));

        PerfilUsuario perfil = perfilUsuarioRepository.buscarPorId(staff.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("PerfilUsuario", "usuarioId", staff.getUsuarioId()));
        Sede sede = sedeRepository.findById(staff.getSedeId()).orElse(null);
        List<String> roles = usuarioRolRepository.listarCodigosRolPorUsuario(staff.getUsuarioId());
        return buildResponse(staff, perfil, sede, roles, null);
    }


    @Override
    @Transactional
    public UsuarioAdminResponse ejecutar(Long id, String nuevoRol, UUID solicitanteId) {
        String rolNorm = nuevoRol.toUpperCase();

        if (SUPERADMIN.equals(rolNorm)) {
            throw new ValidationException("nuevoRol", "No se puede asignar el rol SUPERADMIN.");
        }
        if (!List.of(ADMIN, CAJERO).contains(rolNorm)) {
            throw new ValidationException("nuevoRol", "Rol no válido. Use ADMIN o CAJERO.");
        }

        if (ADMIN.equals(rolNorm)) {
            List<String> rolesSolicitante = usuarioRolRepository.listarCodigosRolPorUsuario(solicitanteId);
            if (!rolesSolicitante.contains(SUPERADMIN)) {
                throw new ValidationException("nuevoRol", "Solo un SUPERADMIN puede asignar el rol ADMIN.");
            }
        }

        StaffPerfil staff = staffPerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerfil", id));

        if (staff.getUsuarioId().equals(solicitanteId)) {
            throw new ValidationException("id", "No puedes cambiar tu propio rol.");
        }

        List<String> rolesActuales = usuarioRolRepository.listarCodigosRolPorUsuario(staff.getUsuarioId());
        String rolActual = rolesActuales.isEmpty() ? "" : rolesActuales.get(0);

        if (rolActual.equals(SUPERADMIN)) {
            throw new ValidationException("id", "No se puede cambiar el rol de un SUPERADMIN.");
        }

        if (!rolNorm.equals(rolActual)) {
            validarLimiteRol(rolNorm, id);
        }

        if (!rolActual.isEmpty()) {
            usuarioRolRepository.eliminar(staff.getUsuarioId(), rolActual);
        }
        usuarioRolRepository.guardar(UsuarioRol.builder()
                .usuarioId(staff.getUsuarioId())
                .rolCodigo(rolNorm)
                .build());

        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                solicitanteId, AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_USUARIOS,
                "Staff", id,
                rolActual, rolNorm,
                "Cambio de rol: " + rolActual + " → " + rolNorm + " para staff #" + id,
                null, null, AuditoriaConstants.NIVEL_WARNING, AuditoriaConstants.RESULTADO_EXITOSO));

        PerfilUsuario perfil = perfilUsuarioRepository.buscarPorId(staff.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("PerfilUsuario", "usuarioId", staff.getUsuarioId()));
        Sede sede = sedeRepository.findById(staff.getSedeId()).orElse(null);
        return buildResponse(staff, perfil, sede, List.of(rolNorm), null);
    }


    @Override
    @Transactional(readOnly = true)
    public void resetear(Long id) {
        StaffPerfil staff = staffPerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerfil", id));
        if (!staff.isEsActivo()) {
            throw new ValidationException("id",
                    "No se puede restablecer la contraseña de un usuario inactivo.");
        }
        PerfilUsuario perfil = perfilUsuarioRepository.buscarPorId(staff.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("PerfilUsuario", "usuarioId", staff.getUsuarioId()));
        supabaseAuthPort.recuperarPassword(perfil.getCorreo());
    }



    @Override
    @Transactional
    public void activar(Long id) {
        StaffPerfil staff = staffPerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerfil", id));
        staffPerfilRepository.guardar(staff.toBuilder()
                .esActivo(true)
                .bloqueadoHasta(null)
                .intentosFallidos(0)
                .build());

        UUID actor = authFacade.usuarioActualId().orElse(null);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                actor, AuditoriaConstants.ACCION_ACTIVAR, AuditoriaConstants.MOD_USUARIOS,
                "Staff", id,
                "inactivo", "activo",
                "Usuario staff #" + id + " activado",
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        StaffPerfil staff = staffPerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerfil", id));

        List<String> roles = usuarioRolRepository.listarCodigosRolPorUsuario(staff.getUsuarioId());
        if (roles.contains(ADMIN) && staffPerfilRepository.contarActivosPorRol(ADMIN) <= 1) {
            throw new ValidationException("id",
                    "No se puede desactivar al único administrador activo del sistema.");
        }

        staffPerfilRepository.guardar(staff.toBuilder().esActivo(false).build());

        UUID actor = authFacade.usuarioActualId().orElse(null);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                actor, AuditoriaConstants.ACCION_DESACTIVAR, AuditoriaConstants.MOD_USUARIOS,
                "Staff", id,
                "activo", "inactivo",
                "Usuario staff #" + id + " desactivado",
                null, null, AuditoriaConstants.NIVEL_WARNING, AuditoriaConstants.RESULTADO_EXITOSO));
    }


    @Override
    @Transactional
    public void desbloquear(Long id) {
        StaffPerfil staff = staffPerfilRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerfil", id));
        staffPerfilRepository.guardar(staff.toBuilder()
                .bloqueadoHasta(null)
                .intentosFallidos(0)
                .build());

        UUID actor = authFacade.usuarioActualId().orElse(null);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                actor, AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_USUARIOS,
                "Staff", id,
                "bloqueado", "desbloqueado",
                "Cuenta desbloqueada manualmente para staff #" + id,
                null, null, AuditoriaConstants.NIVEL_WARNING, AuditoriaConstants.RESULTADO_EXITOSO));
    }

    private void validarLimiteRol(String rol, Long excludeStaffId) {
        long activos = (excludeStaffId != null && excludeStaffId > 0)
                ? staffPerfilRepository.contarActivosPorRolExcluyendo(rol, excludeStaffId)
                : staffPerfilRepository.contarActivosPorRol(rol);
        if (ADMIN.equals(rol) && activos >= MAX_ADMINS) {
            throw new ValidationException("rolCodigo",
                    "Se alcanzó el límite máximo de " + MAX_ADMINS + " administradores activos.");
        }
        if (CAJERO.equals(rol) && activos >= MAX_CAJEROS) {
            throw new ValidationException("rolCodigo",
                    "Se alcanzó el límite máximo de " + MAX_CAJEROS + " cajeros activos.");
        }
    }

    private void validarFortalezaPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("password", "La contraseña debe tener al menos 8 caracteres.");
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            throw new ValidationException("password", "La contraseña debe contener al menos una letra mayúscula.");
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            throw new ValidationException("password", "La contraseña debe contener al menos una letra minúscula.");
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            throw new ValidationException("password", "La contraseña debe contener al menos un número.");
        }
        boolean tieneEspecial = password.chars().anyMatch(c -> PW_SPECIAL.indexOf(c) >= 0);
        if (!tieneEspecial) {
            throw new ValidationException("password",
                    "La contraseña debe contener al menos un carácter especial (" + PW_SPECIAL + ").");
        }
    }

    private String generarPasswordTemporal() {
        char[] arr = new char[12];
        arr[0] = PW_UPPER.charAt(RANDOM.nextInt(PW_UPPER.length()));
        arr[1] = PW_LOWER.charAt(RANDOM.nextInt(PW_LOWER.length()));
        arr[2] = PW_DIGITS.charAt(RANDOM.nextInt(PW_DIGITS.length()));
        arr[3] = PW_SPECIAL.charAt(RANDOM.nextInt(PW_SPECIAL.length()));
        for (int i = 4; i < 12; i++) {
            arr[i] = PW_ALL.charAt(RANDOM.nextInt(PW_ALL.length()));
        }
        for (int i = arr.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
        return new String(arr);
    }

    private UsuarioAdminResponse buildResponse(StaffPerfil staff, PerfilUsuario perfil,
                                               Sede sede, List<String> roles,
                                               String passwordTemporal) {
        return UsuarioAdminResponse.builder()
                .id(staff.getId())
                .usuarioId(staff.getUsuarioId())
                .nombre(perfil.getNombreCompleto())
                .correo(perfil.getCorreo())
                .telefono(perfil.getTelefono())
                .rol(roles.isEmpty() ? "" : roles.get(0))
                .idSede(staff.getSedeId())
                .sedeNombre(sede != null ? sede.getNombre() : "N/A")
                .activo(staff.isEsActivo())
                .debeCambiarContrasena(staff.isDebeCambiarContrasena())
                .intentosFallidos(staff.getIntentosFallidos())
                .bloqueadoHasta(staff.getBloqueadoHasta())
                .ultimoAcceso(perfil.getUltimoLoginAt())
                .fechaCreacion(staff.getCreatedAt())
                .passwordTemporal(passwordTemporal)
                .build();
    }
}
