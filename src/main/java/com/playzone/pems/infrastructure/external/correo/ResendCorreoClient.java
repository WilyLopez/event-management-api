package com.playzone.pems.infrastructure.external.correo;

import com.playzone.pems.application.cms.port.in.GestionarConfiguracionPublicaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResendCorreoClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final GestionarConfiguracionPublicaUseCase configuracionPublica;

    @Value("${playzone.resend.api-key:re_7nURdiD7_N6aSCFavBNntctnpdNcEr6Ch}")
    private String apiKey;

    @Value("${playzone.resend.from:onboarding@resend.dev}")
    private String fromEmail;

    public void enviar(String destinatario, String asunto, String cuerpoHtml) {
        enviarConAdjuntos(destinatario, asunto, cuerpoHtml, List.of());
    }

    public void enviarConLogo(String destinatario, String asunto, String cuerpoHtml) {
        try {
            byte[] logoBytes = new org.springframework.core.io.ClassPathResource("static/logo.png")
                    .getInputStream().readAllBytes();
            
            AttachmentInfo logoAttachment = new AttachmentInfo("logo.png", logoBytes, "logo");
            enviarConAdjuntos(destinatario, asunto, cuerpoHtml, List.of(logoAttachment));
        } catch (Exception e) {
            log.error("Error al cargar logo para enviar via Resend a {}: {}", destinatario, e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo electronico con logo por Resend.", e);
        }
    }

    public void enviarConAdjuntos(String destinatario, String asunto, String cuerpoHtml, List<AttachmentInfo> adjuntos) {
        try {
            String url = "https://api.resend.com/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            String nombreNegocio = configuracionPublica.obtener().getNombreNegocio();
            if (nombreNegocio == null || nombreNegocio.isBlank()) {
                nombreNegocio = "PlayZone";
            }
            String fromValue = nombreNegocio + " <" + fromEmail + ">";

            Map<String, Object> body = new HashMap<>();
            body.put("from", fromValue);
            body.put("to", List.of(destinatario));
            body.put("subject", asunto);
            body.put("html", cuerpoHtml);

            if (adjuntos != null && !adjuntos.isEmpty()) {
                List<Map<String, Object>> attachmentsList = new ArrayList<>();
                for (AttachmentInfo adj : adjuntos) {
                    Map<String, Object> att = new HashMap<>();
                    att.put("filename", adj.getFilename());
                    att.put("content", Base64.getEncoder().encodeToString(adj.getContent()));
                    if (adj.getContentId() != null) {
                        att.put("contentId", adj.getContentId());
                    }
                    attachmentsList.add(att);
                }
                body.put("attachments", attachmentsList);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForObject(url, request, String.class);
            log.info("Correo enviado via Resend a {}: {}", destinatario, asunto);
        } catch (Exception e) {
            log.error("Error al enviar correo via Resend a {}: {}", destinatario, e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo electronico por Resend.", e);
        }
    }

    public static class AttachmentInfo {
        private final String filename;
        private final byte[] content;
        private final String contentId;

        public AttachmentInfo(String filename, byte[] content) {
            this(filename, content, null);
        }

        public AttachmentInfo(String filename, byte[] content, String contentId) {
            this.filename = filename;
            this.content = content;
            this.contentId = contentId;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getContent() {
            return content;
        }

        public String getContentId() {
            return contentId;
        }
    }
}
