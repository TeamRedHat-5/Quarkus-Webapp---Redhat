package org.acme.infrastructure.api.pagination;

import io.quarkus.panache.common.Page;

/**
 * <p>Helper for turning raw {@code pageIndex}/{@code pageSize} request
 * parameters into a valid Panache {@link Page}.</p>
 *
 * <p>Centralizes the defaulting/bounds-checking every paginated endpoint
 * needs, so a caller can't accidentally request a zero/negative page size
 * (which Panache rejects) or an unbounded page size (which reintroduces the
 * memory-spike problem pagination exists to prevent).</p>
 */
public final class PaginationUtil {

    /** Page index used when the caller does not supply one. */
    public static final int DEFAULT_PAGE_INDEX = 0;

    /** Page size used when the caller does not supply one. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Largest page size a client may request, regardless of input. */
    public static final int MAX_PAGE_SIZE = 100;

    private PaginationUtil() {
    }

    /**
     * <p>Builds a {@link Page} from possibly-absent/invalid request
     * parameters, applying defaults and clamping to a sane range:</p>
     * <ul>
     *   <li>{@code pageIndex}: defaults to {@value #DEFAULT_PAGE_INDEX}; negative values are clamped to 0</li>
     *   <li>{@code pageSize}: defaults to {@value #DEFAULT_PAGE_SIZE}; values below 1 are clamped to 1,
     *       values above {@value #MAX_PAGE_SIZE} are clamped to {@value #MAX_PAGE_SIZE}</li>
     * </ul>
     *
     * @param pageIndex The requested zero-based page index, or null to use the default
     * @param pageSize The requested page size, or null to use the default
     * @return A valid Page ready to pass to {@link PagedResult#from}
     */
    public static Page resolve(Integer pageIndex, Integer pageSize) {
        int resolvedIndex = pageIndex == null ? DEFAULT_PAGE_INDEX : Math.max(pageIndex, 0);

        int resolvedSize = pageSize == null
                ? DEFAULT_PAGE_SIZE
                : Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);

        return Page.of(resolvedIndex, resolvedSize);
    }
}
