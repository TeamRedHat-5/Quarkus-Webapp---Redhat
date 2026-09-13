package org.acme.domain.model;

import java.lang.reflect.Field;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class BaseEntityTest {

    @Test
        void declaresAuditingMappingAndLifecycleAnnotations()
            throws NoSuchFieldException, NoSuchMethodException {
        assertNotNull(BaseEntity.class.getAnnotation(MappedSuperclass.class));

        Field createdAt = BaseEntity.class.getDeclaredField("createdAt");
        Column createdAtColumn = createdAt.getAnnotation(Column.class);
        assertEquals("created_at", createdAtColumn.name());
        assertFalse(createdAtColumn.nullable());
        assertFalse(createdAtColumn.updatable());

        Field updatedAt = BaseEntity.class.getDeclaredField("updatedAt");
        Column updatedAtColumn = updatedAt.getAnnotation(Column.class);
        assertEquals("updated_at", updatedAtColumn.name());
        assertFalse(updatedAtColumn.nullable());

        assertNotNull(BaseEntity.class.getDeclaredMethod("onCreate")
                .getAnnotation(PrePersist.class));
        assertNotNull(BaseEntity.class.getDeclaredMethod("onUpdate")
                .getAnnotation(PreUpdate.class));
    }

    @Test
    void populatesCreationAndUpdateTimestamps() {
        BaseEntity entity = new BaseEntity() {};

        entity.onCreate();

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals(entity.getCreatedAt(), entity.getUpdatedAt());

        Instant originalCreatedAt = entity.getCreatedAt();
        Instant originalUpdatedAt = entity.getUpdatedAt();

        entity.onUpdate();

        assertEquals(originalCreatedAt, entity.getCreatedAt());
        assertFalse(entity.getUpdatedAt().isBefore(originalUpdatedAt));
    }

    @Test
    void exposesAuditFieldsInJson() throws Exception {
        BaseEntity entity = new BaseEntity() {};
        entity.onCreate();

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        String json = objectMapper.writeValueAsString(entity);

        assertTrue(json.contains("\"createdAt\""));
        assertTrue(json.contains("\"updatedAt\""));
    }
}