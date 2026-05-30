package com.playzone.pems.infrastructure.external.correo;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import com.playzone.pems.application.evento.port.out.EnviarNotificacionEventoPort;
import com.playzone.pems.application.evento.port.out.EnviarTicketPorCorreoPort;
import com.playzone.pems.application.usuario.port.out.EnviarCorreoVerificacionPort;
import com.playzone.pems.domain.usuario.repository.SedeRepository;
import com.playzone.pems.infrastructure.pdf.PdfTicketService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CorreoAdapter
        implements EnviarCorreoVerificacionPort,
        EnviarTicketPorCorreoPort,
        EnviarNotificacionEventoPort {

    private final JavaMailCorreoClient correoClient;
    private final JavaMailSender       mailSender;
    private final PdfTicketService     pdfTicketService;
    private final SedeRepository       sedeRepository;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${playzone.correo.nombre-remitente:PlayZone}")
    private String nombreRemitente;

    @Value("${playzone.negocio.correo-admin:admin@kikiylala.com}")
    private String correoAdmin;

    @Override
    public void enviarBienvenida(String destinatario, String nombreCliente) {
        String asunto = "Bienvenida a Kiki y Lala";
        String cuerpo = cargarTemplate("bienvenida.html")
                .replace("{{nombre}}", nombreCliente);
        correoClient.enviarConLogo(destinatario, asunto, cuerpo);
    }

    @Override
    public void enviarTicket(String destinatario, String nombreCliente, ReservaPublicaQuery reserva) {
        String nombreSede = sedeRepository.findById(reserva.getIdSede())
                .map(s -> s.getNombre())
                .orElse("Sede Principal");

        byte[] pdfBytes = pdfTicketService.generarTicketPdf(reserva, nombreSede);

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente, nombreRemitente);
            helper.setTo(destinatario);
            helper.setSubject("Tu ticket Kiki y Lala — " + reserva.getNumeroTicket());

            String cuerpoHtml =
                "<div style='font-family:Arial,sans-serif;max-width:500px;margin:0 auto;'>" +
                "<div style='background:#00AEEF;padding:20px;border-radius:12px 12px 0 0;text-align:center;'>" +
                "<h2 style='color:white;margin:0;'>Tu reserva esta confirmada</h2>" +
                "</div>" +
                "<div style='background:#f8fafc;padding:20px;border:1px solid #e2e8f0;border-radius:0 0 12px 12px;'>" +
                "<p style='color:#1A1A2E;font-size:15px;'>Hola <strong>" + nombreCliente + "</strong>,</p>" +
                "<p style='color:#475569;'>Tu ticket para la zona de juegos de Kiki y Lala esta listo.</p>" +
                "<div style='background:white;border:1px solid #e2e8f0;border-radius:8px;padding:16px;margin:16px 0;'>" +
                "<p style='margin:4px 0;font-size:13px;color:#64748b;'>Numero de ticket</p>" +
                "<p style='margin:4px 0;font-size:16px;font-weight:900;font-family:monospace;color:#00AEEF;'>" + reserva.getNumeroTicket() + "</p>" +
                "<p style='margin:12px 0 4px;font-size:13px;color:#64748b;'>Fecha</p>" +
                "<p style='margin:4px 0;font-size:15px;font-weight:700;color:#1A1A2E;'>" + reserva.getFechaEvento().toString() + "</p>" +
                "<p style='margin:12px 0 4px;font-size:13px;color:#64748b;'>Total</p>" +
                "<p style='margin:4px 0;font-size:15px;font-weight:700;color:#15803d;'>S/ " + reserva.getTotalPagado() + "</p>" +
                "</div>" +
                "<p style='color:#f59e0b;font-size:13px;'>Adjuntamos tu ticket en PDF. Presentalo en recepcion el dia de tu visita.</p>" +
                "<p style='color:#64748b;font-size:12px;margin-top:16px;'>Kiki y Lala &mdash; El local mas divertido de Chiclayo</p>" +
                "</div></div>";

            helper.setText(cuerpoHtml, true);
            helper.addAttachment(
                "Ticket-" + reserva.getNumeroTicket() + ".pdf",
                new ByteArrayResource(pdfBytes),
                "application/pdf"
            );

            mailSender.send(mensaje);
            log.info("Ticket PDF enviado a {}: {}", destinatario, reserva.getNumeroTicket());
        } catch (Exception e) {
            log.error("Error al enviar ticket a {}: {}", destinatario, e.getMessage(), e);
            throw new RuntimeException("Error al enviar el ticket por correo.", e);
        }
    }

    @Override
    public void notificarSolicitudRecibida(String destinatario, EventoPrivadoQuery evento) {
        String asunto = "Solicitud de evento privado recibida — Kiki y Lala";
        String cuerpo = htmlBase(
            "Solicitud recibida",
            "<p>Hola <b>" + evento.getNombreCliente() + "</b>,</p>"
            + "<p>Hemos recibido tu solicitud de evento privado. Nuestro equipo se pondrá en contacto contigo en menos de 24 horas.</p>"
            + resumenEvento(evento)
            + "<p style='color:#64748b;font-size:13px;margin-top:20px;'>Próximos pasos: te confirmaremos disponibilidad, precio y condiciones por este medio.</p>"
        );
        correoClient.enviarConLogo(destinatario, asunto, cuerpo);
    }

    @Override
    public void notificarEventoConfirmado(String destinatario, EventoPrivadoQuery evento) {
        String asunto = "Tu evento privado ha sido confirmado — Kiki y Lala";
        String cuerpo = htmlBase(
            "¡Evento confirmado!",
            "<p>Hola <b>" + evento.getNombreCliente() + "</b>,</p>"
            + "<p>Tu evento ha sido confirmado. Te compartimos el resumen:</p>"
            + resumenEvento(evento)
            + filaFinanciera("Total contratado", "S/ " + evento.getPrecioTotalContrato())
            + filaFinanciera("Adelanto pagado", "S/ " + evento.getMontoAdelanto())
            + filaFinanciera("Saldo pendiente", "S/ " + evento.getMontoSaldo())
            + "<p style='color:#64748b;font-size:13px;margin-top:16px;'>El contrato será enviado próximamente. El saldo pendiente se abona el día del evento.</p>"
        );
        correoClient.enviarConLogo(destinatario, asunto, cuerpo);
    }

    @Override
    public void notificarEventoCancelado(String destinatario, EventoPrivadoQuery evento, String motivo) {
        String asunto = "Evento privado cancelado — Kiki y Lala";
        String cuerpo = htmlBase(
            "Evento cancelado",
            "<p>Hola <b>" + evento.getNombreCliente() + "</b>,</p>"
            + "<p>Lamentamos informarte que el evento del <b>" + evento.getFechaEvento() + "</b> ha sido cancelado.</p>"
            + "<p><b>Motivo:</b> " + motivo + "</p>"
            + "<p style='color:#64748b;font-size:13px;'>Si tienes dudas, contáctanos por WhatsApp o correo.</p>"
        );
        correoClient.enviarConLogo(destinatario, asunto, cuerpo);
    }

    @Override
    public void notificarAdminNuevaSolicitud(EventoPrivadoQuery evento) {
        String asunto = "[Nueva solicitud] " + evento.getTipoEvento() + " — " + evento.getFechaEvento();
        String cuerpo = htmlBase(
            "Nueva solicitud de evento",
            "<p>Se ha recibido una nueva solicitud de evento privado:</p>"
            + resumenEvento(evento)
            + "<p><b>Cliente:</b> " + evento.getNombreCliente() + "</p>"
            + "<p><b>Correo:</b> " + evento.getCorreoCliente() + "</p>"
            + "<p><b>Teléfono:</b> " + (evento.getTelefonoCliente() != null ? evento.getTelefonoCliente() : "—") + "</p>"
            + "<p style='margin-top:16px;'><a href='#' style='background:#00AEEF;color:white;padding:10px 20px;border-radius:8px;text-decoration:none;font-weight:bold;'>Ver en el panel</a></p>"
        );
        correoClient.enviar(correoAdmin, asunto, cuerpo);
    }

    private String htmlBase(String titulo, String contenido) {
        return "<div style='font-family:Arial,sans-serif;max-width:520px;margin:0 auto;'>"
            + "<div style='background:#F64B8A;padding:20px;border-radius:12px 12px 0 0;text-align:center;'>"
            + "<h2 style='color:white;margin:0;font-size:18px;'>" + titulo + "</h2>"
            + "</div>"
            + "<div style='background:#f8fafc;padding:24px;border:1px solid #e2e8f0;border-radius:0 0 12px 12px;'>"
            + contenido
            + "</div></div>";
    }

    private String resumenEvento(EventoPrivadoQuery evento) {
        return "<div style='background:white;border:1px solid #e2e8f0;border-radius:8px;padding:16px;margin:16px 0;font-size:13px;'>"
            + filaEvento("Tipo de evento", evento.getTipoEvento())
            + filaEvento("Fecha", evento.getFechaEvento() != null ? evento.getFechaEvento().toString() : "—")
            + filaEvento("Turno", evento.getTurno() + " · " + evento.getHoraInicio() + " – " + evento.getHoraFin())
            + (evento.getAforoDeclarado() != null ? filaEvento("Invitados", evento.getAforoDeclarado() + " personas") : "")
            + "</div>";
    }

    private String filaEvento(String label, String valor) {
        return "<p style='margin:6px 0;'><span style='color:#64748b;'>" + label + ":</span> <b>" + valor + "</b></p>";
    }

    private String filaFinanciera(String label, String valor) {
        return "<p style='margin:6px 0;display:flex;justify-content:space-between;'>"
            + "<span style='color:#64748b;'>" + label + "</span>"
            + "<b style='color:#1A1A2E;'>" + valor + "</b></p>";
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
