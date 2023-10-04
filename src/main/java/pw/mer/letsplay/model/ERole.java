package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collection;
import java.util.List;

public enum ERole {
    ADMIN,
    USER;

    @JsonValue
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public Collection<String> getScopes() {
        if (this == ADMIN) {
            return List.of("users", "products:write");
        } else {
            return List.of();
        }
    }
}
