package org.acme.infrastructure.security;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class ApiKeyRequestFilter implements Filter {

    @Override
    public Response filter(final FilterableRequestSpecification requestSpecification,
                           final FilterableResponseSpecification responseSpecification,
                           final FilterContext context) {
        requestSpecification.header("X-API-KEY", "change-me");
        return context.next(requestSpecification, responseSpecification);
    }
}