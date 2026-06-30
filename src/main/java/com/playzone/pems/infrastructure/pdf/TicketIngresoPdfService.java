package com.playzone.pems.infrastructure.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class TicketIngresoPdfService {

    private final TemplateEngine templateEngine;

    public byte[] generarTicketPdf(ReservaPublicaQuery reserva, String nombreSede) {
        System.setProperty("sun.net.client.defaultConnectTimeout", "1500");
        System.setProperty("sun.net.client.defaultReadTimeout", "1500");

        Context ctx = new Context();
        ctx.setVariable("reserva",    reserva);
        ctx.setVariable("nombreSede", nombreSede);

        String html = templateEngine.process("ticket-ingreso", ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del ticket: " + e.getMessage(), e);
        }
    }
}
