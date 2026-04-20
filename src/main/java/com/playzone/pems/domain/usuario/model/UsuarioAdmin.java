package com.playzone.pems.domain.usuario.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdmin {

    private Long          id;
    private Long          idSede;
    private String        nombre;
    private String        correo;
    private String        contrasenaHash;
    private boolean       activo;
    private int           intentosFallidos;
    private LocalDateTime bloqueadoHasta;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public boolean estaBloqueado(LocalDateTime ahora) {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(ahora);
    }

    public boolean puedeIniciarSesion(LocalDateTime ahora) {
        return activo && !estaBloqueado(ahora);
    }
}