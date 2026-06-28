package com.playzone.pems.domain.finanzas.query;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MontoPorDia(LocalDate fecha, BigDecimal monto) {}
