package com.playzone.pems.application.usuario.service;

import com.playzone.pems.application.usuario.dto.command.RegistrarClientePublicoCommand;
import com.playzone.pems.application.usuario.port.out.SupabaseAuthPort;
import com.playzone.pems.domain.usuario.model.ClientePerfil;
import com.playzone.pems.domain.usuario.repository.ClientePerfilRepository;
import com.playzone.pems.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientePerfilServiceTest {

    @Mock
    private ClientePerfilRepository clientePerfilRepository;
    @Mock
    private SupabaseAuthPort supabaseAuthPort;

    @InjectMocks
    private ClientePerfilService clientePerfilService;

    private RegistrarClientePublicoCommand command;

    @BeforeEach
    void setUp() {
        command = RegistrarClientePublicoCommand.builder()
                .nombre("Juan Perez")
                .correo("juan@gmail.com")
                .password("123456")
                .telefono("999888777")
                .tipoDocumentoCodigo("DNI")
                .numeroDocumento("12345678")
                .build();
    }

    @Test
    void registrarNuevoClienteCorrectamente() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        when(clientePerfilRepository.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(clientePerfilRepository.buscarPorDocumento(anyString(), anyString())).thenReturn(Optional.empty());
        when(supabaseAuthPort.crearUsuario(anyString(), anyString(), anyString())).thenReturn(usuarioId);
        when(clientePerfilRepository.guardar(any(ClientePerfil.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ClientePerfil resultado = clientePerfilService.ejecutar(command);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(command.getCorreo(), resultado.getCorreo());
        assertEquals("WEB", resultado.getOrigen());
        verify(supabaseAuthPort).crearUsuario(command.getCorreo(), command.getPassword(), command.getNombre());
        verify(clientePerfilRepository).guardar(any(ClientePerfil.class));
    }

    @Test
    void vincularClientePOSExtistente() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        ClientePerfil clienteExistente = ClientePerfil.builder()
                .id(1L)
                .correo(command.getCorreo())
                .usuarioId(null) // POS client
                .tipoDocumentoCodigo("DNI")
                .numeroDocumento("12345678")
                .build();

        when(clientePerfilRepository.buscarPorCorreo(command.getCorreo())).thenReturn(Optional.of(clienteExistente));
        when(supabaseAuthPort.crearUsuario(anyString(), anyString(), anyString())).thenReturn(usuarioId);
        when(clientePerfilRepository.guardar(any(ClientePerfil.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ClientePerfil resultado = clientePerfilService.ejecutar(command);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(1L, resultado.getId());
        assertEquals("WEB", resultado.getOrigen());
        verify(supabaseAuthPort).crearUsuario(command.getCorreo(), command.getPassword(), command.getNombre());
        verify(clientePerfilRepository, never()).buscarPorDocumento(anyString(), anyString());
    }

    @Test
    void fallarSiCorreoYaEstaVinculado() {
        // Arrange
        ClientePerfil clienteExistente = ClientePerfil.builder()
                .id(1L)
                .correo(command.getCorreo())
                .usuarioId(UUID.randomUUID()) // Already linked
                .build();

        when(clientePerfilRepository.buscarPorCorreo(command.getCorreo())).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThrows(ValidationException.class, () -> clientePerfilService.ejecutar(command));
    }

    @Test
    void fallarSiDocumentoDuplicado() {
        // Arrange
        when(clientePerfilRepository.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(clientePerfilRepository.buscarPorDocumento(anyString(), anyString()))
                .thenReturn(Optional.of(ClientePerfil.builder().build()));

        // Act & Assert
        assertThrows(ValidationException.class, () -> clientePerfilService.ejecutar(command));
    }

    @Test
    void fallarSiSupabaseError() {
        // Arrange
        when(clientePerfilRepository.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(clientePerfilRepository.buscarPorDocumento(anyString(), anyString())).thenReturn(Optional.empty());
        when(supabaseAuthPort.crearUsuario(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Supabase Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> clientePerfilService.ejecutar(command));
    }
}
