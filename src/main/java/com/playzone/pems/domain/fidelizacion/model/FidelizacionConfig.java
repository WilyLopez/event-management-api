package com.playzone.pems.domain.fidelizacion.model;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionConfig {
    private Long id;
    private Long idSede;
    private int umbral;
    private OffsetDateTime updatedAt;
}
