package com.playzone.pems.shared.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PagedResponse<T> {

    private final List<T> content;
    private final int     paginaActual;
    private final int     totalPaginas;
    private final long    totalElementos;
    private final int     tamanioPagina;
    private final boolean primera;
    private final boolean ultima;
    private final boolean vacia;

    private PagedResponse(Page<T> page) {
        this.content        = page.getContent();
        this.paginaActual   = page.getNumber();
        this.totalPaginas   = page.getTotalPages();
        this.totalElementos = page.getTotalElements();
        this.tamanioPagina  = page.getSize();
        this.primera        = page.isFirst();
        this.ultima         = page.isLast();
        this.vacia          = page.isEmpty();
    }

    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(page);
    }
}