package pw.mer.letsplay.web.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// https://www.baeldung.com/java-bean-validation-constraint-composition

public interface UserValidators {
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Constraint(validatedBy = {})
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    @interface Password {
        String message() default "Password is not valid";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Constraint(validatedBy = {})
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    @interface Name {
        String message() default "Name is not valid";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @jakarta.validation.constraints.Email(message = "Email is not valid")
    @Size(min = 3, message = "Email is not valid") // for some reason, @Email accepts empty strings
    @Constraint(validatedBy = {})
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    @interface Email {
        String message() default "Email is not valid";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }
}
