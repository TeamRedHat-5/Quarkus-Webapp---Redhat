package org.acme.infrastructure.security;

import org.junit.jupiter.api.Test;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityHeadersFilterTest {

    @Test
    void addsSecurityHeadersToResponse() throws Exception {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        ContainerResponseContext responseContext = (ContainerResponseContext) Proxy.newProxyInstance(
                ContainerResponseContext.class.getClassLoader(),
                new Class<?>[]{ContainerResponseContext.class},
                (proxy, method, args) -> method.getName().equals("getHeaders") ? headers : null);

        new SecurityHeadersFilter().filter(null, responseContext);

        assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"));
        assertEquals("DENY", headers.getFirst("X-Frame-Options"));
        assertEquals("1; mode=block", headers.getFirst("X-XSS-Protection"));
        assertEquals("max-age=31536000; includeSubDomains",
                headers.getFirst("Strict-Transport-Security"));
    }
}
