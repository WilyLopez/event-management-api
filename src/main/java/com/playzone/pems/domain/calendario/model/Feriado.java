package com.playzone.pems.domain.calendario.model;

import com.playzone.pems.domain.calendario.model.enums.TipoFeriado;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Feriado {

    private Long          id;
    private TipoFeriado   tipoFeriado;
    private LocalDate     fecha;
    private String        descripcion;
    private int           anio;
    private LocalDateTime fechaCreacion;
    private Long          idUsuarioCreador;
    public boolean esNacional() {
        return TipoFeriado.NACIONAL == tipoFeriado;
    }

    public String etiqueta() {
        return String.format("%s — %s (%s)",
                tipoFeriado.getDescripcion(), descripcion, fecha);
    }
}