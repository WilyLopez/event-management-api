package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class HacerVipRequest {

    @Min(value = 0,   message = "El descuento no puede ser negativo.")
    @Max(value = 100, message = "El descuento no puede superar el 100%.")
    private int descuento = 10;
}