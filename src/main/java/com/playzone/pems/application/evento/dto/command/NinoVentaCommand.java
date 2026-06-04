package com.playzone.pems.application.evento.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NinoVentaCommand {
    private String nombre;
    private int    edad;
}
