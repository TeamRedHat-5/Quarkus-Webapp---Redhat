package org.acme.infrastructure.api.pagination;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PagedResultTest {

    /**
     * Builds a proxy PanacheQuery whose page()/list()/count() behave like a
     * real query over {@code totalElements} rows, without needing a running
     * Hibernate/Panache runtime.
     */
    @SuppressWarnings("unchecked")
    private static PanacheQuery<String> fakeQuery(List<String> allRows, long totalElements) {
        InvocationHandler handler = new InvocationHandler() {
            private Page appliedPage;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                switch (method.getName()) {
                    case "page":
                        if (args[0] instanceof Page) {
                            appliedPage = (Page) args[0];
                        }
                        return proxy;
                    case "list":
                        return allRows;
                    case "count":
                        return totalElements;
                    default:
                        return null;
                }
            }
        };

        return (PanacheQuery<String>) Proxy.newProxyInstance(
                PagedResultTest.class.getClassLoader(),
                new Class<?>[]{PanacheQuery.class},
                handler);
    }

    @Test
    void fromAppliesPageAndBuildsMetadata() {
        List<String> pageOfData = Arrays.asList("a", "b", "c");
        PanacheQuery<String> query = fakeQuery(pageOfData, 25L);

        PagedResult<String> result = PagedResult.from(query, Page.of(1, 3));

        assertEquals(pageOfData, result.getData());
        assertEquals(1, result.getPageIndex());
        assertEquals(3, result.getPageSize());
        assertEquals(25L, result.getTotalElements());
        // ceil(25 / 3) = 9
        assertEquals(9, result.getTotalPages());
    }

    @Test
    void fromRejectsNullQuery() {
        assertThrows(IllegalArgumentException.class, () -> PagedResult.from(null, Page.of(0, 10)));
    }

    @Test
    void fromRejectsNullPage() {
        PanacheQuery<String> query = fakeQuery(List.of(), 0L);
        assertThrows(IllegalArgumentException.class, () -> PagedResult.from(query, null));
    }

    @Test
    void emptyReturnsNoDataAndZeroTotals() {
        PagedResult<String> result = PagedResult.empty(2, 10);

        assertTrue(result.getData().isEmpty());
        assertEquals(2, result.getPageIndex());
        assertEquals(10, result.getPageSize());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }
}
