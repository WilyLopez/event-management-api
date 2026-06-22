package com.playzone.pems.domain.usuario.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuario {

    private UUID          id;
    private String        nombreCompleto;
    private String        correo;
    private String        telefono;
    private String        fotoPerfilPath;
    private OffsetDateTime ultimoLoginAt;
    private OffsetDateTime creadoEn;
}
