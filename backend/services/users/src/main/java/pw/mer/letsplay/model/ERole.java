package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public enum ERole {
    ADMIN,
    USER,
    SELLER;

    @JsonValue
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public List<String> getScopes() {
        return switch (this) {
            case ADMIN -> List.of("users:admin", "products:admin", "media:write", "orders:admin");
            case SELLER -> List.of("products:write", "media:write", "orders:write");
            default -> List.of();
        };
    }

    public static ERole fromString(String role) throws IllegalArgumentException {
        if (role == null) {
            return null;
        }
        return ERole.valueOf(role.toUpperCase());
    }

    public static final String VALIDATION_MESSAGE = "Invalid role. Valid roles are: " +
            String.join(", ", List.of(ERole.values()).toString());
}