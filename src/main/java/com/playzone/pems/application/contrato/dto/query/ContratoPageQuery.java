package com.playzone.pems.application.contrato.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContratoPageQuery {
    private List<ContratoQuery> content;
    private int                 page;
    private int                 size;
    private long                totalElements;
    private int                 totalPages;
}