package com.playzone.pems.application.venta.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NinoMostradorCommand {
    private String nombreNino;
    private int    edadNino;
}
