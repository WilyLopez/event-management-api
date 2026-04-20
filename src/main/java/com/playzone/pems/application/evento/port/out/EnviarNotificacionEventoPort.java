package com.playzone.pems.application.evento.port.out;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

public interface EnviarNotificacionEventoPort {

    void notificarSolicitudRecibida(String destinatario, EventoPrivadoQuery evento);

    void notificarEventoConfirmado(String destinatario, EventoPrivadoQuery evento);

    void notificarEventoCancelado(String destinatario, EventoPrivadoQuery evento, String motivo);
}