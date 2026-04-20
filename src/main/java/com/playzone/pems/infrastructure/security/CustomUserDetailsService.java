package com.playzone.pems.infrastructure.security;

import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.model.UsuarioAdmin;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.domain.usuario.repository.UsuarioAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClienteRepository      clienteRepository;
    private final UsuarioAdminRepository adminRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        return adminRepository.findByCorreo(correo)
                .map(this::buildDesdeAdmin)
                .orElseGet(() -> clienteRepository.findByCorreo(correo)
                        .map(this::buildDesdeCliente)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "Usuario no encontrado con correo: " + correo)));
    }

    private UserDetails buildDesdeAdmin(UsuarioAdmin admin) {
        return User.builder()
                .username(admin.getCorreo())
                .password(admin.getContrasenaHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .accountLocked(!admin.isActivo())
                .build();
    }

    private UserDetails buildDesdeCliente(Cliente cliente) {
        return User.builder()
                .username(cliente.getCorreo())
                .password(cliente.getContrasenaHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                .accountLocked(!cliente.isActivo())
                .disabled(!cliente.isCorreoVerificado())
                .build();
    }
}