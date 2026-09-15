package org.acme.infrastructure.api.pagination;

import java.util.Collections;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

/**
 * <p>Generic, reusable envelope for paginated list responses.</p>
 *
 * <p>Wraps a page of {@code data} together with the metadata a client needs to
 * render pagination controls or request the next page: the requested
 * {@code pageIndex}/{@code pageSize}, the {@code totalElements} across every
 * page, and the resulting {@code totalPages}.</p>
 *
 * <p>Build instances via {@link #from(PanacheQuery, Page)} rather than the
 * constructor directly, so every endpoint that returns a paginated list
 * produces metadata the same way.</p>
 *
 * @param <T> The type of element contained in the page of data
 */
public class PagedResult<T> {

    private final List<T> data;
    private final int pageIndex;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    /**
     * @param data The elements belonging to the requested page
     * @param pageIndex The zero-based index of the page that was fetched
     * @param pageSize The maximum number of elements requested per page
     * @param totalElements The total number of elements across all pages
     * @param totalPages The total number of pages, given totalElements and pageSize
     */
    public PagedResult(List<T> data, int pageIndex, int pageSize, long totalElements, int totalPages) {
        this.data = data;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    /**
     * <p>Applies the given {@link Page} to a Panache query and builds a
     * {@link PagedResult} containing that page's data plus pagination
     * metadata computed from the query's total element count.</p>
     *
     * <p>Typical usage in a REST resource:</p>
     * <pre>{@code
     * PanacheQuery<Flight> query = Flight.findAll();
     * return PagedResult.from(query, Page.of(pageIndex, pageSize));
     * }</pre>
     *
     * @param query The (optionally filtered/sorted) Panache query to paginate
     * @param page The requested page index and size
     * @param <T> The entity type returned by the query
     * @return A PagedResult containing the requested page of data and pagination metadata
     * @throws IllegalArgumentException if query or page is null
     */
    public static <T> PagedResult<T> from(PanacheQuery<T> query, Page page) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }

        query.page(page);

        List<T> data = query.list();
        long totalElements = query.count();
        int totalPages = page.size > 0
                ? (int) Math.ceil((double) totalElements / (double) page.size)
                : 0;

        return new PagedResult<>(data, page.index, page.size, totalElements, totalPages);
    }

    /**
     * @return An empty PagedResult for the given page request, with zero total elements/pages
     */
    public static <T> PagedResult<T> empty(int pageIndex, int pageSize) {
        return new PagedResult<>(Collections.emptyList(), pageIndex, pageSize, 0L, 0);
    }

    public List<T> getData() {
        return data;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
