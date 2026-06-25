package com.playzone.pems.shared.util;

import org.springframework.data.domain.Sort;

public final class SortUtils {

    private SortUtils() {}

    public static Sort parsearSort(String sort) {
        String[]       parts = sort.split(",");
        Sort.Direction dir   = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                               ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, parts[0]);
    }
}
