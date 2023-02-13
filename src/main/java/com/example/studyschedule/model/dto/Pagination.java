package com.example.studyschedule.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Pagination<T> {

    private PageInfo page;
    private T data;

    public Pagination(Page page, T data) {
        this.page = new PageInfo(page);
        this.data = data;
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class PageInfo {

        private long totalElements;
        private int totalPages;
        private int size;
        private int number;
        private boolean first;
        private boolean last;

        public PageInfo(Page page) {
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.size = page.getSize();
            this.number = page.getNumber();
            this.first = page.isFirst();
            this.last = page.isLast();
        }
    }
}
