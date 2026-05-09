package com.playzone.pems.application.usuario.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClientePageQuery {

    private List<ClienteQuery> content;
    private int                page;
    private int                size;
    private long               totalElements;
    private int                totalPages;
}