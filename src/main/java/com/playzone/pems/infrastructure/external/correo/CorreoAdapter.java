package com.playzone.pems.infrastructure.external.correo;

import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.out.EnviarNotificacionEventoPort;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import com.playzone.pems.application.evento.port.out.EnviarTicketPorCorreoPort;
import com.playzone.pems.application.usuario.port.out.EnviarCorreoVerificacionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CorreoAdapter
        implements EnviarCorreoVerificacionPort,
        EnviarTicketPorCorreoPort,
        EnviarNotificacionEventoPort {

    private final JavaMailCorreoClient correoClient;

    @Value("${playzone.url-base:http://localhost:8080}")
    private String urlBase;

    @Override
    public void enviarVerificacion(String destinatario, String nombreCliente, String urlVerificacion) {
        String asunto = "Verifica tu cuenta en PlayZone";
        String cuerpo = "<h2>Hola, " + nombreCliente + "!</h2>"
                + "<p>Por favor verifica tu correo haciendo clic en el siguiente enlace:</p>"
                + "<a href=\"" + urlBase + urlVerificacion + "\">Verificar cuenta</a>"
                + "<p>El enlace expira en 24 horas.</p>";
        correoClient.enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void enviarTicket(String destinatario, String nombreCliente, ReservaPublicaQuery reserva) {
        String asunto = "Tu ticket PlayZone — " + reserva.getNumeroTicket();
        String cuerpo = "<h2>¡Hola, " + nombreCliente + "!</h2>"
                + "<p>Tu reserva ha sido registrada exitosamente.</p>"
                + "<ul>"
                + "<li><b>Ticket:</b> " + reserva.getNumeroTicket() + "</li>"
                + "<li><b>Fecha:</b> " + reserva.getFechaEvento() + "</li>"
                + "<li><b>Niño:</b> " + reserva.getNombreNino() + " (" + reserva.getEdadNino() + " años)</li>"
                + "<li><b>Total pagado:</b> S/ " + reserva.getTotalPagado() + "</li>"
                + "</ul>"
                + "<p>Preséntate con este número de ticket el día del evento.</p>";
        correoClient.enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void notificarSolicitudRecibida(String destinatario, EventoPrivadoQuery evento) {
        String asunto = "Solicitud de evento privado recibida — PlayZone";
        String cuerpo = "<h2>Solicitud recibida</h2>"
                + "<p>Hemos recibido tu solicitud para el <b>" + evento.getFechaEvento() + "</b>.</p>"
                + "<p>Turno solicitado: <b>" + evento.getTurno() + "</b></p>"
                + "<p>Nos pondremos en contacto contigo pronto para confirmar los detalles.</p>";
        correoClient.enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void notificarEventoConfirmado(String destinatario, EventoPrivadoQuery evento) {
        String asunto = "¡Tu evento privado ha sido confirmado! — PlayZone";
        String cuerpo = "<h2>Evento confirmado</h2>"
                + "<p>Tu evento del <b>" + evento.getFechaEvento() + "</b> ha sido confirmado.</p>"
                + "<p>Turno: <b>" + evento.getTurno() + " (" + evento.getHoraInicio() + " – " + evento.getHoraFin() + ")</b></p>"
                + "<p>Monto total: <b>S/ " + evento.getPrecioTotalContrato() + "</b></p>"
                + "<p>Adelanto recibido: <b>S/ " + evento.getMontoAdelanto() + "</b></p>"
                + "<p>Saldo pendiente: <b>S/ " + evento.getMontoSaldo() + "</b></p>";
        correoClient.enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void notificarEventoCancelado(String destinatario, EventoPrivadoQuery evento, String motivo) {
        String asunto = "Evento privado cancelado — PlayZone";
        String cuerpo = "<h2>Evento cancelado</h2>"
                + "<p>Lamentamos informarte que el evento del <b>" + evento.getFechaEvento() + "</b> ha sido cancelado.</p>"
                + "<p>Motivo: " + motivo + "</p>"
                + "<p>Si tienes dudas, contáctanos por WhatsApp o correo.</p>";
        correoClient.enviar(destinatario, asunto, cuerpo);
    }
}