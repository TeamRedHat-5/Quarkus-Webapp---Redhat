package uk.ac.newcastle.enterprisemiddleware.infrastructure.exceptions;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@QuarkusTest
class GlobalExceptionMapperTest {

    @org.junit.jupiter.api.BeforeEach
    static void configureApiKey() {
        RestAssured.filters(new org.acme.infrastructure.security.ApiKeyRequestFilter());
    }

    @Test
    void restServiceExceptionReturnsStructuredJsonError() {
        given()
                .when()
                .get("/contacts/999999999")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", equalTo("No Contact with the id 999999999 was found!"))
                .body("path", equalTo("/contacts/999999999"))
                .body("timestamp", notNullValue());
    }
}