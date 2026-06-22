package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.port.in.CambiarPasswordMeUseCase;
import com.playzone.pems.application.usuario.port.in.LoginUseCase;
import com.playzone.pems.application.usuario.port.in.RecuperarPasswordUseCase;
import com.playzone.pems.application.usuario.port.out.SupabaseAuthPort;
import com.playzone.pems.domain.usuario.model.StaffPerfil;
import com.playzone.pems.domain.usuario.repository.StaffPerfilRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthContext;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.UnauthorizedException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements LoginUseCase, RecuperarPasswordUseCase, CambiarPasswordMeUseCase {

    private final SupabaseAuthPort       supabaseAuthPort;
    private final StaffPerfilRepository   staffPerfilRepository;
    private final SupabaseAuthFacade      supabaseAuthFacade;

    @Value("${playzone.seguridad.max-intentos-login:5}")
    private int maxIntentos;

    @Value("${playzone.seguridad.duracion-bloqueo-min:15}")
    private int duracionBloqueoMin;

    @Override
    @Transactional
    public Map<String, Object> ejecutar(String email, String password) {
        Optional<StaffPerfil> staffOpt = staffPerfilRepository.buscarPorCorreo(email);

        if (staffOpt.isPresent()) {
            StaffPerfil staff = staffOpt.get();
            if (staff.getBloqueadoHasta() != null && staff.getBloqueadoHasta().isAfter(OffsetDateTime.now())) {
                throw new UnauthorizedException("Usuario bloqueado temporalmente. Intente nuevamente en unos minutos.");
            }
            if (!staff.isEsActivo()) {
                throw new UnauthorizedException("Cuenta desactivada. Contacte al administrador.");
            }
        }

        try {
            Map<String, Object> response = supabaseAuthPort.login(email, password);
            
            // Login exitoso
            if (staffOpt.isPresent()) {
                StaffPerfil staff = staffOpt.get();
                StaffPerfil.StaffPerfilBuilder builder = staff.toBuilder()
                        .intentosFallidos(0)
                        .bloqueadoHasta(null);
                
                staffPerfilRepository.guardar(builder.build());
                
                // Agregar flag a la respuesta para el frontend
                response.put("debeCambiarPassword", staff.isDebeCambiarContrasena());
            }

            return response;
        } catch (HttpClientErrorException e) {
            // Login fallido
            staffOpt.ifPresent(staff -> {
                int nuevosIntentos = (staff.getIntentosFallidos() != null ? staff.getIntentosFallidos() : 0) + 1;
                OffsetDateTime bloqueo = null;
                if (nuevosIntentos >= maxIntentos) {
                    bloqueo = OffsetDateTime.now().plusMinutes(duracionBloqueoMin);
                    log.warn("Usuario {} bloqueado hasta {}", email, bloqueo);
                }
                staffPerfilRepository.guardar(staff.toBuilder()
                        .intentosFallidos(nuevosIntentos)
                        .bloqueadoHasta(bloqueo)
                        .build());
            });
            throw new UnauthorizedException("Credenciales invalidas.");
        }
    }

    @Override
    @Transactional
    public void ejecutar(String email) {
        supabaseAuthPort.recuperarPassword(email);
    }

    @Override
    @Transactional
    public void ejecutar(String accessToken, String passwordActual, String nuevoPassword) {
        SupabaseAuthContext ctx = supabaseAuthFacade.contextoActual()
                .orElseThrow(() -> new UnauthorizedException("No autenticado"));

        // Validar password actual
        try {
            supabaseAuthPort.login(ctx.email(), passwordActual);
        } catch (Exception e) {
            throw new ValidationException("passwordActual", "La contraseña actual no es correcta.");
        }

        // Actualizar en Supabase
        supabaseAuthPort.actualizarPassword(accessToken, nuevoPassword);

        // Actualizar flag en staff_perfil si aplica
        staffPerfilRepository.buscarPorUsuarioId(ctx.userId()).ifPresent(staff -> {
            if (staff.isDebeCambiarContrasena()) {
                staffPerfilRepository.guardar(staff.toBuilder()
                        .debeCambiarContrasena(false)
                        .build());
            }
        });
    }
}
