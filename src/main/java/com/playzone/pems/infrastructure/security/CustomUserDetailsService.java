package com.playzone.pems.infrastructure.security;

import com.playzone.pems.domain.usuario.model.Cliente;
import com.playzone.pems.domain.usuario.model.UsuarioAdmin;
import com.playzone.pems.domain.usuario.repository.ClienteRepository;
import com.playzone.pems.domain.usuario.repository.UsuarioAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        return new CustomUserDetails(
                admin.getId(),
                admin.getCorreo(),
                admin.getContrasenaHash(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                admin.isActivo(),
                true);
    }

    private UserDetails buildDesdeCliente(Cliente cliente) {
        return new CustomUserDetails(
                cliente.getId(),
                cliente.getCorreo(),
                cliente.getContrasenaHash(),
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")),
                cliente.isActivo(),
                cliente.isCorreoVerificado());
    }
}