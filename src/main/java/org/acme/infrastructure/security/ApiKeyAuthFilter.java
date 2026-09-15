package org.acme.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiKeyAuthFilter implements ContainerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";

    private final String configuredApiKey;

    @Inject
    public ApiKeyAuthFilter(@ConfigProperty(name = "security.api-key") final String configuredApiKey) {
        this.configuredApiKey = configuredApiKey;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        String requestApiKey = requestContext.getHeaderString(API_KEY_HEADER);

        if (requestApiKey == null || !keysMatch(requestApiKey, configuredApiKey)) {
            requestContext.abortWith(Response.status(Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Collections.singletonMap("error", UNAUTHORIZED_MESSAGE))
                    .build());
        }
    }

    private boolean keysMatch(final String requestApiKey, final String expectedApiKey) {
        return MessageDigest.isEqual(
                requestApiKey.getBytes(StandardCharsets.UTF_8),
                expectedApiKey.getBytes(StandardCharsets.UTF_8));
    }
}