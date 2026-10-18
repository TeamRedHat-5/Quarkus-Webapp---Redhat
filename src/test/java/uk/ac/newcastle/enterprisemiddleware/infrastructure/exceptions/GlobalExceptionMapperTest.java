package uk.ac.newcastle.enterprisemiddleware.infrastructure.exceptions;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import uk.ac.newcastle.enterprisemiddleware.contact.Contact;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@QuarkusTest
class GlobalExceptionMapperTest {

    @Test
    void restServiceExceptionReturnsStructuredJsonError() {
        given()
                .header("X-API-KEY", "change-me")
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

    @Test
    void staleContactUpdateReturnsStructuredConflict() {
        Contact contact = new Contact();
        contact.setFirstName("Lock");
        contact.setLastName("Probe");
        contact.setEmail("lock-" + UUID.randomUUID() + "@example.test");
        contact.setPhoneNumber("(201) 123-4567");
        contact.setBirthDate(new java.util.Date(0));

        Contact created = given()
                .header("X-API-KEY", "change-me")
                .contentType(ContentType.JSON)
                .body(contact)
                .when()
                .post("/contacts")
                .then()
                .statusCode(201)
                .extract()
                .as(Contact.class);

        Contact viewA = given()
                .header("X-API-KEY", "change-me")
                .when()
                .get("/contacts/{id}", created.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(Contact.class);
        Contact viewB = given()
                .header("X-API-KEY", "change-me")
                .when()
                .get("/contacts/{id}", created.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(Contact.class);
        assertEquals(viewA.getVersion(), viewB.getVersion());

        viewA.setFirstName("UpdatedA");
        given()
                .header("X-API-KEY", "change-me")
                .contentType(ContentType.JSON)
                .body(viewA)
                .when()
                .put("/contacts/{id}", created.getId())
                .then()
                .statusCode(200);

        viewB.setFirstName("StaleB");
        given()
                .header("X-API-KEY", "change-me")
                .contentType(ContentType.JSON)
                .body(viewB)
                .when()
                .put("/contacts/{id}", created.getId())
                .then()
                .statusCode(409)
                .contentType(ContentType.JSON)
                .body("status", equalTo(409))
                .body("error", equalTo("Conflict"))
                .body("message", equalTo("The resource was modified by another request. Please reload and try again."));

        Contact latest = given()
                .header("X-API-KEY", "change-me")
                .when()
                .get("/contacts/{id}", created.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(Contact.class);
        assertEquals("UpdatedA", latest.getFirstName());

        given()
                .header("X-API-KEY", "change-me")
                .when()
                .delete("/contacts/{id}", created.getId())
                .then()
                .statusCode(204);
    }
}