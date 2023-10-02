package pw.mer.letsplay.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
