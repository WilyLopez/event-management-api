package com.playzone.pems.domain.finanzas.query;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastosPorDia(LocalDate fecha, BigDecimal monto) {}
