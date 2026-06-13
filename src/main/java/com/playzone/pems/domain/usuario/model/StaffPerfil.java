package com.playzone.pems.domain.usuario.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StaffPerfil {

    private Long      id;
    private UUID      usuarioId;
    private Long      sedeId;
    private String    codigoEmpleado;
    private LocalDate fechaIngreso;
    private String    telefonoEmergencia;
    private String    observaciones;
    private boolean   esActivo;
}
