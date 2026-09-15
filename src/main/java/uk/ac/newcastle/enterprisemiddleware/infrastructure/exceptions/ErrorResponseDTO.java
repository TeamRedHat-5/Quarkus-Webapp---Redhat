package uk.ac.newcastle.enterprisemiddleware.infrastructure.exceptions;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.Map;

@RegisterForReflection
public class ErrorResponseDTO {

    private final String timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String, String> reasons;

    public ErrorResponseDTO(
            String timestamp,
            int status,
            String error,
            String message,
            String path) {
        this(timestamp, status, error, message, path, Collections.emptyMap());
    }

    public ErrorResponseDTO(
            String timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> reasons) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.reasons = reasons;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getReasons() {
        return reasons;
    }
}
