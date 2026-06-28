package com.playzone.pems.application.finanzas.dto.query;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SerieDiaQuery(LocalDate fecha, BigDecimal ingresos, BigDecimal egresos) {}
