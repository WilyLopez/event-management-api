package com.playzone.pems.domain.contrato.model;

import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Contrato {

    private Long           id;
    private Long           idEventoPrivado;
    private EstadoContrato estado;
    private String         contenidoTexto;
    private String         archivoPdfUrl;
    private LocalDate      fechaFirma;
    private Long           idUsuarioRedactor;
    private LocalDateTime  fechaCreacion;
    private LocalDateTime  fechaActualizacion;

    public boolean esEditable() {
        return estado.esEditable();
    }

    public boolean estaFirmado() {
        return estado.esFirmado();
    }

    public boolean tienePdfGenerado() {
        return archivoPdfUrl != null && !archivoPdfUrl.isBlank();
    }
}