package com.playzone.pems.application.comercial.dto.command;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearZonaCommand {

    @NotBlank
    @Size(max = 25)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    private String descripcion;

    @Min(0)
    private Integer edadMinima;

    @Max(17)
    private Integer edadMaxima;

    @Size(max = 8)
    private List<String> imagenes;

    @Size(max = 3)
    private List<@Pattern(
            regexp = "^https://(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/|tiktok\\.com/).+$"
    ) String> videos;
}
