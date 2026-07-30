package ru.practicum.gateway.ewm.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record OffsetPageRequest(long offset, int pageSize, Sort sort) implements Pageable {
    public OffsetPageRequest(long offset, int pageSize) {
        this(offset, pageSize, Sort.unsorted());
    }

    public OffsetPageRequest {
        if (offset < 0) {
            throw new IllegalArgumentException("from must be greater than or equal to 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("size must be greater than 0");
        }
    }

    @Override
    public int getPageNumber() {
        return Math.toIntExact(offset / pageSize);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetPageRequest(offset + pageSize, pageSize, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? new OffsetPageRequest(offset - pageSize, pageSize, sort) : first();
    }

    @Override
    public Pageable first() {
        return new OffsetPageRequest(0, pageSize, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetPageRequest((long) pageNumber * pageSize, pageSize, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset >= pageSize;
    }
}
