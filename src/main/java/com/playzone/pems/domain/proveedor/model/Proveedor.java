package com.playzone.pems.domain.proveedor.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    private Long          id;
    private String        nombre;
    private String        ruc;
    private String        contactoNombre;
    private String        contactoTelefono;
    private String        contactoCorreo;
    private String        tipoServicio;
    private String        notas;
    private boolean       activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public boolean emibeComprobantes() {
        return ruc != null && !ruc.isBlank();
    }

    public String contactoPrincipal() {
        if (contactoCorreo != null && !contactoCorreo.isBlank()) return contactoCorreo;
        if (contactoTelefono != null && !contactoTelefono.isBlank()) return contactoTelefono;
        return "Sin contacto registrado";
    }
}