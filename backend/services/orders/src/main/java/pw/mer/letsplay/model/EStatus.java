package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public enum EStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELED;

    @JsonValue
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static EStatus fromString(String status) throws IllegalArgumentException {
        if (status == null) {
            return null;
        }
        return EStatus.valueOf(status.toUpperCase());
    }

    public static final String VALIDATION_MESSAGE = "Invalid status. Valid statuses are: " +
            String.join(", ", List.of(EStatus.values()).toString());
}