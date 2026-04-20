package com.playzone.pems.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String  message;
    private final T       data;
    private final Instant timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.timestamp = Instant.now();
    }

    /** 200 OK con datos y mensaje genérico. */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    /** 200 OK con datos y mensaje personalizado. */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** 201 Created — recurso creado exitosamente. */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "Recurso creado exitosamente.", data);
    }

    /** 204 No Content — operación realizada sin cuerpo de respuesta. */
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(true, "Operación realizada exitosamente.", null);
    }

    /** Respuesta de error genérica con mensaje. */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}