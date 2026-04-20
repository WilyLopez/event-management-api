package com.playzone.pems.shared.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public final class FechaUtil {

    public static final ZoneId ZONA_PERU = ZoneId.of("America/Lima");

    private static final Locale              LOCALE_PE    = new Locale("es", "PE");
    private static final DateTimeFormatter   FMT_FECHA    = DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE_PE);
    private static final DateTimeFormatter   FMT_FECHAHORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", LOCALE_PE);
    private static final DateTimeFormatter   FMT_ISO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;

    private FechaUtil() {}

    public static LocalDate hoyPeru() {
        return LocalDate.now(ZONA_PERU);
    }

    public static LocalDateTime ahoraPeru() {
        return LocalDateTime.now(ZONA_PERU);
    }

    public static Instant ahoraInstante() {
        return Instant.now();
    }

    public static boolean esFindeSemana(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    public static boolean esLaborable(LocalDate fecha) {
        return !esFindeSemana(fecha);
    }

    public static long diferenciaEnHoras(Instant desde, Instant hasta) {
        return ChronoUnit.HOURS.between(desde, hasta);
    }

    public static long diferenciaEnDias(LocalDate desde, LocalDate hasta) {
        return ChronoUnit.DAYS.between(desde, hasta);
    }

    public static long diferenciaEnMinutos(LocalDateTime desde, LocalDateTime hasta) {
        return ChronoUnit.MINUTES.between(desde, hasta);
    }

    public static boolean esPasado(LocalDate fecha) {
        return fecha.isBefore(hoyPeru());
    }

    public static boolean estaEnRango(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

    public static boolean superaAnticipacionMinima(LocalDate fechaEvento, long minimoHoras) {
        Instant inicioEvento = fechaEvento.atStartOfDay(ZONA_PERU).toInstant();
        long horasRestantes  = ChronoUnit.HOURS.between(Instant.now(), inicioEvento);
        return horasRestantes >= minimoHoras;
    }

    public static String formatear(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FMT_FECHA);
    }

    public static String formatear(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FMT_FECHAHORA);
    }

    public static String formatearIso(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FMT_ISO_FECHA);
    }

    public static ZonedDateTime inicioDia(LocalDate fecha) {
        return fecha.atStartOfDay(ZONA_PERU);
    }

    public static LocalDate instanteAFecha(Instant instante) {
        return instante.atZone(ZONA_PERU).toLocalDate();
    }
}