package com.playzone.pems.shared.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class TicketUtil {

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final String PREFIJO_TICKET = "TKT";
    private static final String REGEX_TICKET = "^TKT-\\d+-\\d{8}-\\d{4}$";
    private TicketUtil() {}

    public static String generar(int idSede, LocalDate fecha, long secuencia) {
        return String.format("%s-%d-%s-%04d",
                PREFIJO_TICKET,
                idSede,
                fecha.format(FMT_FECHA),
                secuencia);
    }

    public static String generarReferenciaPago(int idMedioPago) {
        return String.format("PAY-%d-%d", idMedioPago, System.currentTimeMillis());
    }

    public static boolean esValido(String numeroTicket) {
        return numeroTicket != null && numeroTicket.matches(REGEX_TICKET);
    }

    public static LocalDate extraerFecha(String numeroTicket) {
        if (!esValido(numeroTicket)) return null;
        try {
            String[] partes = numeroTicket.split("-");
            return LocalDate.parse(partes[2], FMT_FECHA);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer extraerIdSede(String numeroTicket) {
        if (!esValido(numeroTicket)) return null;
        try {
            return Integer.parseInt(numeroTicket.split("-")[1]);
        } catch (Exception e) {
            return null;
        }
    }
}