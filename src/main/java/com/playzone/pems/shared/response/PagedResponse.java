package com.playzone.pems.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PagedResponse<T> {

    private final List<T> content;
    private final int     page;
    private final int     totalPages;
    private final long    totalElements;
    private final int     size;
    private final boolean first;
    private final boolean last;
    private final boolean empty;

    private PagedResponse(Page<T> page) {
        this.content       = page.getContent();
        this.page          = page.getNumber();
        this.totalPages    = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size          = page.getSize();
        this.first         = page.isFirst();
        this.last          = page.isLast();
        this.empty         = page.isEmpty();
    }

    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(page);
    }
}