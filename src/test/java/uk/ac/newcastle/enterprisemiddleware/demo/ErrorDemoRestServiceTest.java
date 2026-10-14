package uk.ac.newcastle.enterprisemiddleware.demo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ErrorDemoRestServiceTest {

    @Test
    void unexpectedErrorIsReturnedAsSanitizedStructuredServerError() {
        given()
                .header("X-API-KEY", "change-me")
                .when()
                .get("/demo/errors/unexpected")
                .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("timestamp", notNullValue())
                .body("status", equalTo(500))
                .body("error", equalTo("Internal Server Error"))
                .body("message", equalTo("An unexpected error occurred while processing the request."))
                .body("path", equalTo("/demo/errors/unexpected"));
    }

    @Test
    void missingContactPreservesNotFoundStatusInStructuredResponse() {
        given()
                .header("X-API-KEY", "change-me")
                .when()
                .get("/contacts/999999999")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("timestamp", notNullValue())
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", containsString("No Contact with the id"))
                .body("path", equalTo("/contacts/999999999"));
    }

    @Test
    void invalidContactReturnsStructuredFieldValidationErrors() {
        given()
                .header("X-API-KEY", "change-me")
                .contentType(ContentType.JSON)
                .body("{\"firstName\":\"A name that is far too long for this field\","
                        + "\"lastName\":\"\","
                        + "\"email\":\"not-an-email\","
                        + "\"phoneNumber\":\"invalid\","
                        + "\"birthDate\":\"2099-01-01T00:00:00.000Z\"}")
                .when()
                .post("/contacts")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("violations.size()", greaterThan(0))
                .body("violations.field", hasItem("createContact.contact.firstName"));
    }

    @Test
    void validContactIsCreatedAndResponseContainsConfiguredSecurityHeaders() {
        given()
                .header("X-API-KEY", "change-me")
                .contentType(ContentType.JSON)
                .body("{\"firstName\":\"Demo\","
                        + "\"lastName\":\"Contact\","
                        + "\"email\":\"demo-" + System.nanoTime() + "@example.com\","
                        + "\"phoneNumber\":\"(201) 123-4567\","
                        + "\"birthDate\":\"1990-01-01T00:00:00.000Z\","
                        + "\"state\":\"New Jersey\"}")
                .when()
                .post("/contacts")
                .then()
                .statusCode(201)
                .header("X-Content-Type-Options", equalTo("nosniff"))
                .header("X-Frame-Options", equalTo("DENY"))
                .header("X-XSS-Protection", equalTo("1; mode=block"))
                .header("Strict-Transport-Security",
                        equalTo("max-age=31536000; includeSubDomains"));
    }
}
