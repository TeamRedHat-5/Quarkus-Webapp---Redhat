package org.acme.infrastructure.api.pagination;

import org.junit.jupiter.api.Test;

import io.quarkus.panache.common.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationUtilTest {

    @Test
    void usesDefaultsWhenParamsAreNull() {
        Page page = PaginationUtil.resolve(null, null);

        assertEquals(PaginationUtil.DEFAULT_PAGE_INDEX, page.index);
        assertEquals(PaginationUtil.DEFAULT_PAGE_SIZE, page.size);
    }

    @Test
    void clampsNegativePageIndexToZero() {
        Page page = PaginationUtil.resolve(-5, 10);

        assertEquals(0, page.index);
        assertEquals(10, page.size);
    }

    @Test
    void clampsPageSizeBelowOneUpToOne() {
        Page page = PaginationUtil.resolve(0, -3);

        assertEquals(1, page.size);
    }

    @Test
    void clampsPageSizeAboveMaxDownToMax() {
        Page page = PaginationUtil.resolve(0, 10_000);

        assertEquals(PaginationUtil.MAX_PAGE_SIZE, page.size);
    }

    @Test
    void passesThroughValidValues() {
        Page page = PaginationUtil.resolve(3, 50);

        assertEquals(3, page.index);
        assertEquals(50, page.size);
    }
}
