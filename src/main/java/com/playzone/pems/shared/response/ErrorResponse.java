package com.playzone.pems.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final int     status;
    private final String  error;
    private final String  codigoError;
    private final String  message;
    private final String  path;
    private final Instant timestamp;
    private final List<CampoError> erroresCampo;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CampoError {

        private final String campo;
        private final String mensaje;
        private final Object valorRechazado;
    }
}