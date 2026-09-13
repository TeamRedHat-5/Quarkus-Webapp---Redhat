package uk.ac.newcastle.enterprisemiddleware.infrastructure.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectMapperConfigTest {

    private final ObjectMapper objectMapper = configuredObjectMapper();

    @Test
    void excludesNullValuesAndUsesIsoDates() throws Exception {
        JsonPayload payload = new JsonPayload();
        payload.name = "example";
        payload.date = LocalDate.of(2026, 9, 13);

        String json = objectMapper.writeValueAsString(payload);

        assertTrue(json.contains("\"date\":\"2026-09-13\""));
        assertTrue(json.contains("\"name\":\"example\""));
        assertFalse(json.contains("optional"));
    }

    @Test
    void ignoresUnknownProperties() throws Exception {
        JsonPayload payload = objectMapper.readValue(
                "{\"name\":\"example\",\"unexpected\":true}",
                JsonPayload.class);

        assertNotNull(payload);
        assertEquals("example", payload.name);
    }

    private static ObjectMapper configuredObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        new ObjectMapperConfig().customize(objectMapper);
        return objectMapper;
    }

    public static class JsonPayload {
        public String name;
        public LocalDate date;
        public String optional;
    }
}