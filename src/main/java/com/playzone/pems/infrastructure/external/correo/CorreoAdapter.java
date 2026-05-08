package com.playzone.pems.infrastructure.external.correo;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.out.EnviarNotificacionEventoPort;
import com.playzone.pems.application.evento.port.out.EnviarTicketPorCorreoPort;
import com.playzone.pems.application.usuario.port.out.EnviarCorreoVerificacionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CorreoAdapter
        implements EnviarCorreoVerificacionPort,
        EnviarTicketPorCorreoPort,
        EnviarNotificacionEventoPort {

    private final JavaMailCorreoClient correoClient;

    @Override
    public void enviarBienvenida(String destinatario, String nombreCliente) {
        String asunto = "¡Bienvenida a Kiki y Lala! 🎉";
        String cuerpo = cargarTemplate("bienvenida.html")
                .replace("{{nombre}}", nombreCliente);
        correoClient.enviarConLogo(destinatario, asunto, cuerpo);
    }

    @Override
    public void enviarTicket(String destinatario, String nombreCliente, ReservaPublicaQuery reserva) {
        String asunto = "Tu ticket Kiki y Lala — " + reserva.getNumeroTicket();
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
        String asunto = "Solicitud de evento privado recibida — Kiki y Lala";
        String cuerpo = "<h2>Solicitud recibida</h2>"
                + "<p>Hemos recibido tu solicitud para el <b>" + evento.getFechaEvento() + "</b>.</p>"
                + "<p>Turno solicitado: <b>" + evento.getTurno() + "</b></p>"
                + "<p>Nos pondremos en contacto contigo pronto para confirmar los detalles.</p>";
        correoClient.enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void notificarEventoConfirmado(String destinatario, EventoPrivadoQuery evento) {
        String asunto = "¡Tu evento privado ha sido confirmado! — Kiki y Lala";
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
        String asunto = "Evento privado cancelado — Kiki y Lala";
        String cuerpo = "<h2>Evento cancelado</h2>"
                + "<p>Lamentamos informarte que el evento del <b>" + evento.getFechaEvento() + "</b> ha sido cancelado.</p>"
                + "<p>Motivo: " + motivo + "</p>"
                + "<p>Si tienes dudas, contáctanos por WhatsApp o correo.</p>";
        correoClient.enviar(destinatario, asunto, cuerpo);
    }

    private String cargarTemplate(String nombre) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + nombre);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el template de correo: " + nombre, e);
        }
    }
}
