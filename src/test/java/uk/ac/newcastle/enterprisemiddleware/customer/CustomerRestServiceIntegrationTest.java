package uk.ac.newcastle.enterprisemiddleware.customer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@TestHTTPEndpoint(CustomerRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)

public class CustomerRestServiceIntegrationTest {

    private static Customer customer;

    private static RequestSpecification when() {
        return given().header("X-API-KEY", "change-me");
    }
    @BeforeAll
    static void setup() {

        customer = new Customer();
        customer.setName("Testron");
        customer.setEmail("test@gmail.com");
        customer.setPhoneNumber("09890448159");
        System.out.println(customer.toString());

    }

    @BeforeEach
    void configureApiKey() {
        RestAssured.filters(new org.acme.infrastructure.security.ApiKeyRequestFilter());
    }

    @Test
    @Order(1)
    public void testCanCreateCustomer() {
        System.out.println(customer.toString());
        given().
                contentType(ContentType.JSON).
                body(customer).
                when()
                .post().
                then().
                statusCode(201);
    }

    @Test
    @Order(2)
    public void testCanGetCustomer() {
        Response response = when().
                get().
                then().
                statusCode(200).
                extract().response();

        Customer[] result = response.body().as(Customer[].class);

        System.out.println(result[0]);

        assertEquals(1, result.length);
        assertTrue(customer.getName().equals(result[0].getName()), "First name not equal");
        assertTrue(customer.getEmail().equals(result[0].getEmail()), "Email not equal");
        assertTrue(customer.getPhoneNumber().equals(result[0].getPhoneNumber()), "Phone number not equal");
    }

    @Test
    @Order(3)
    public void testDuplicateEmailCausesError() {
        given().
                contentType(ContentType.JSON).
                body(customer).
                when().
                post().
                then().
                statusCode(409).
                body("reasons.email", containsString("email is already used"));
    }

    @Test
    @Order(4)
    public void testCanDeleteCustomer() {
        Response response = when().
                get().
                then().
                statusCode(200).
                extract().response();

        Customer[] result = response.body().as(Customer[].class);

        when().
                delete(result[0].getId().toString()).
                then().
                statusCode(204);
    }

}
