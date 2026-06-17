package com.playzone.pems.domain.evento.query;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IngresosPorDia(LocalDate fecha, BigDecimal monto) {}
