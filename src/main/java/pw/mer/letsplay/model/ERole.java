package pw.mer.letsplay.model;

import java.util.Collection;
import java.util.List;

public enum ERole {
    ADMIN,
    USER;

    public Collection<String> getScopes() {
        if (this == ADMIN) {
            return List.of("users");
        } else {
            return List.of();
        }
    }
}
