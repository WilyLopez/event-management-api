package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.port.out.SupabaseAuthPort;
import com.playzone.pems.domain.usuario.model.StaffPerfil;
import com.playzone.pems.domain.usuario.repository.StaffPerfilRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthContext;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.UnauthorizedException;
import com.playzone.pems.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private SupabaseAuthPort       supabaseAuthPort;
    @Mock
    private StaffPerfilRepository   staffPerfilRepository;
    @Mock
    private SupabaseAuthFacade      supabaseAuthFacade;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "maxIntentos", 3);
        ReflectionTestUtils.setField(authService, "duracionBloqueoMin", 15);
    }

    @Test
    void loginExitosoReseteaIntentos() {
        // Arrange
        String email = "test@test.com";
        String pass = "123456";
        StaffPerfil staff = StaffPerfil.builder()
                .usuarioId(UUID.randomUUID())
                .intentosFallidos(2)
                .esActivo(true)
                .build();
        
        when(staffPerfilRepository.buscarPorCorreo(email)).thenReturn(Optional.of(staff));
        when(supabaseAuthPort.login(email, pass)).thenReturn(new HashMap<>());

        // Act
        authService.ejecutar(email, pass);

        // Assert
        verify(staffPerfilRepository).guardar(argThat(s -> s.getIntentosFallidos() == 0));
    }

    @Test
    void loginFallidoIncrementaIntentos() {
        // Arrange
        String email = "test@test.com";
        StaffPerfil staff = StaffPerfil.builder()
                .intentosFallidos(0)
                .esActivo(true)
                .build();
        when(staffPerfilRepository.buscarPorCorreo(email)).thenReturn(Optional.of(staff));
        when(supabaseAuthPort.login(anyString(), anyString())).thenThrow(mock(HttpClientErrorException.class));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.ejecutar(email, "error"));
        verify(staffPerfilRepository).guardar(argThat(s -> s.getIntentosFallidos() == 1));
    }

    @Test
    void bloquearUsuarioAlAlcanzarLimite() {
        // Arrange
        String email = "test@test.com";
        StaffPerfil staff = StaffPerfil.builder()
                .intentosFallidos(2)
                .esActivo(true)
                .build();
        when(staffPerfilRepository.buscarPorCorreo(email)).thenReturn(Optional.of(staff));
        when(supabaseAuthPort.login(anyString(), anyString())).thenThrow(mock(HttpClientErrorException.class));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.ejecutar(email, "error"));
        verify(staffPerfilRepository).guardar(argThat(s -> s.getBloqueadoHasta() != null));
    }

    @Test
    void impedirLoginSiEstaBloqueado() {
        // Arrange
        String email = "test@test.com";
        StaffPerfil staff = StaffPerfil.builder()
                .bloqueadoHasta(OffsetDateTime.now().plusMinutes(5))
                .build();
        when(staffPerfilRepository.buscarPorCorreo(email)).thenReturn(Optional.of(staff));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.ejecutar(email, "pass"));
        verify(supabaseAuthPort, never()).login(anyString(), anyString());
    }

    @Test
    void cambiarPasswordExitoso() {
        // Arrange
        UUID userId = UUID.randomUUID();
        SupabaseAuthContext ctx = new SupabaseAuthContext(userId, "test@test.com", "ADMIN", null, null, null, 0L);
        StaffPerfil staff = StaffPerfil.builder().usuarioId(userId).debeCambiarContrasena(true).build();

        when(supabaseAuthFacade.contextoActual()).thenReturn(Optional.of(ctx));
        when(supabaseAuthPort.login(anyString(), anyString())).thenReturn(new HashMap<>());
        when(staffPerfilRepository.buscarPorUsuarioId(userId)).thenReturn(Optional.of(staff));

        // Act
        authService.ejecutar("token", "old", "new");

        // Assert
        verify(supabaseAuthPort).actualizarPassword("token", "new");
        verify(staffPerfilRepository).guardar(argThat(s -> !s.isDebeCambiarContrasena()));
    }

    @Test
    void fallarCambioPasswordSiActualEsInvalida() {
        // Arrange
        UUID userId = UUID.randomUUID();
        SupabaseAuthContext ctx = new SupabaseAuthContext(userId, "test@test.com", "ADMIN", null, null, null, 0L);
        when(supabaseAuthFacade.contextoActual()).thenReturn(Optional.of(ctx));
        when(supabaseAuthPort.login(anyString(), anyString())).thenThrow(mock(HttpClientErrorException.class));

        // Act & Assert
        assertThrows(ValidationException.class, () -> authService.ejecutar("token", "wrong", "new"));
        verify(supabaseAuthPort, never()).actualizarPassword(anyString(), anyString());
    }
}
