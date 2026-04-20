package com.playzone.pems.domain.fidelizacion.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HistorialFidelizacion {

    private Long          id;
    private Long          idCliente;
    private Long          idReservaPublica;
    private int           visitaNumero;
    private boolean       esBeneficioAplicado;
    private LocalDateTime fechaRegistro;

    public boolean fueVisitaBeneficio() {
        return esBeneficioAplicado;
    }

    public boolean correspondeOtorgarBeneficio(int visitasParaBeneficio) {
        return visitaNumero > 0
                && visitasParaBeneficio > 0
                && visitaNumero % visitasParaBeneficio == 0;
    }
}