package com.playzone.pems.infrastructure.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.playzone.pems.application.venta.dto.query.VentaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class NotaVentaPdfService {

    private final TemplateEngine templateEngine;

    public byte[] generarNotaVentaPdf(VentaQuery venta, String nombreSede) {
        Context ctx = new Context();
        ctx.setVariable("venta",      venta);
        ctx.setVariable("nombreSede", nombreSede);

        String html = templateEngine.process("nota-venta", ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la Nota de Venta: " + e.getMessage(), e);
        }
    }
}
