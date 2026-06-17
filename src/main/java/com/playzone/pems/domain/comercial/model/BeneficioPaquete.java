package com.playzone.pems.domain.comercial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BeneficioPaquete {
    private Long id;
    private Long idPaquete;
    private String descripcion;
    private int orden;
    private OffsetDateTime createdAt;
}
