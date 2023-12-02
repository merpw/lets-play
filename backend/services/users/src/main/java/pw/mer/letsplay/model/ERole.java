package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
            case ADMIN -> List.of("users:admin", "products:write", "media:write");
            case SELLER -> List.of("products:write", "media:write");
            default -> List.of();
        };
    }

    public static ERole fromString(String role) throws IllegalArgumentException {
        if (role == null) {
            return null;
        }
        return ERole.valueOf(role.toUpperCase());
    }

    // Custom validation annotation (@ERole.Valid) to check if the string is a valid role
    // https://www.baeldung.com/javax-validations-enums

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = Validator.class)
    public @interface Valid {
        String message() default "Role must be any of 'admin', 'user' or 'seller'";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class Validator implements jakarta.validation.ConstraintValidator<Valid, String> {
        @Override
        public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
            try {
                ERole.fromString(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}