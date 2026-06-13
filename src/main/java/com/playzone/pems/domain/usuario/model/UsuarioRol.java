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
public class UsuarioRol {

    private UUID           usuarioId;
    private String         rolCodigo;
    private OffsetDateTime asignadoAt;
}
