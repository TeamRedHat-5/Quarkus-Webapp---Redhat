package org.acme.infrastructure.security;

import java.lang.reflect.Proxy;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class ApiKeyAuthFilterTest {

    private static final String EXPECTED_API_KEY = "test-api-key";

    @Test
    void allowsRequestWithConfiguredApiKey() throws Exception {
        RequestContextProbe requestContext = new RequestContextProbe(EXPECTED_API_KEY);

        new ApiKeyAuthFilter(EXPECTED_API_KEY).filter(requestContext.proxy());

        assertFalse(requestContext.wasAborted());
    }

    @Test
    void rejectsRequestWithMissingApiKey() throws Exception {
        RequestContextProbe requestContext = new RequestContextProbe(null);

        new ApiKeyAuthFilter(EXPECTED_API_KEY).filter(requestContext.proxy());

        assertUnauthorized(requestContext);
    }

    @Test
    void rejectsRequestWithInvalidApiKey() throws Exception {
        RequestContextProbe requestContext = new RequestContextProbe("wrong-api-key");

        new ApiKeyAuthFilter(EXPECTED_API_KEY).filter(requestContext.proxy());

        assertUnauthorized(requestContext);
    }

    private void assertUnauthorized(final RequestContextProbe requestContext) {
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), requestContext.response.getStatus());
        assertEquals("application/json", requestContext.response.getMediaType().toString());
        assertEquals("Unauthorized", ((java.util.Map<?, ?>) requestContext.response.getEntity()).get("error"));
    }

    private static final class RequestContextProbe {
        private final String apiKey;
        private Response response;

        private RequestContextProbe(final String apiKey) {
            this.apiKey = apiKey;
        }

        private ContainerRequestContext proxy() {
            return (ContainerRequestContext) Proxy.newProxyInstance(
                    ContainerRequestContext.class.getClassLoader(),
                    new Class<?>[]{ContainerRequestContext.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getHeaderString")) {
                            return apiKey;
                        }
                        if (method.getName().equals("abortWith")) {
                            response = (Response) args[0];
                        }
                        return null;
                    });
        }

        private boolean wasAborted() {
            return response != null;
        }
    }
}