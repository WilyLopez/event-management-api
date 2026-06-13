package com.playzone.pems.domain.evento.query;

import java.time.LocalDate;

public record ReservasPorDia(LocalDate fecha, long cantidad) {}
