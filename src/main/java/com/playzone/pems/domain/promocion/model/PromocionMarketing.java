package com.playzone.pems.domain.promocion.model;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PromocionMarketing {
    private String  imagenPath;
    private String  bannerPath;
    private String  colorDestacado;
    private String  textoPublicitario;
    private String  textoBoton;
    private String  urlBoton;
    private boolean mostrarEnInicio;
    private boolean mostrarEnCarrusel;
    private boolean mostrarEnPromociones;
    private boolean mostrarEnCheckout;
    private boolean soloMovil;
}
