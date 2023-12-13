package pw.mer.letsplay.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepo userRepo;

    private static final String NOT_FOUND_MESSAGE = "User not found";

    @Autowired
    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @SecurityRequirement(name = "Authentication", scopes = {"users:admin"})
    @GetMapping
    public List<User> index() {
        return userRepo.findAll();
    }

    @SecurityRequirement(name = "Authentication")
    @GetMapping("/{id}")
    public User getById(@PathVariable String id, Authentication authentication) {
        var user = userRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        if (isAdmin(authentication)) {
            return user;
        }

        if (user.getRole() == ERole.USER) {
            throw new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE);
        }
        return user;
    }

    @SecurityRequirement(name = "Authentication", scopes = {"users:admin"})
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        if (userRepo.findById(id).isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE);
        }
        userRepo.deleteById(id);
    }

    @Setter
    public static class UpdateUserRequest {
        @JsonProperty("name")
        private Optional
                <@Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
                        String> name = Optional.empty();

        @JsonProperty("email")
        private Optional
                <@Size(min = 3, message = "Email is not valid") @Email(message = "Email is not valid")
                        String> email = Optional.empty();

        @JsonProperty("password")
        private Optional
                <@Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
                        String> rawPassword = Optional.empty();

        @JsonProperty("role")
        private Optional<ERole> role = Optional.empty();

        @JsonProperty("image")
        private Optional<String> image = Optional.empty();
    }

    PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @SecurityRequirement(name = "Authentication", scopes = {"users:admin"})
    @PutMapping("/{id}")
    public void updateById(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request, Authentication authentication) {
        var user = userRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        if (!isAdmin(authentication) && !authentication.getName().equals(user.getId())) {
            throw new ResponseStatusException(FORBIDDEN);
        }

        request.email.ifPresent(email -> {
            if (!email.equals(user.getEmail()) && !userRepo.findByEmail(email).isEmpty()) {
                throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists");
            }
            user.setEmail(email);
        });

        request.name.ifPresent(user::setName);
        request.role.ifPresent(user::setRole);

        request.image.ifPresent(user::setImage);

        request.rawPassword.ifPresent(rawPassword -> {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setEncodedPassword(encodedPassword);
        });

        userRepo.save(user);
    }

    @Setter
    public static class AddUserRequest {
        @JsonProperty("name")
        @NotBlank(message = "Name is mandatory")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        private String name;

        @JsonProperty("email")
        @NotBlank(message = "Email is mandatory")
        @Size(min = 3, max = 320, message = "Email is not valid")
        @Email(message = "Email is not valid")
        private String email;

        @JsonProperty("password")
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        private String rawPassword;

        @JsonProperty("role")
        @NotBlank(message = "Role is mandatory")
        private String role;

        @JsonProperty("image")
        private Optional<String> image = Optional.empty();
    }

    @SecurityRequirement(name = "Authentication", scopes = {"users:admin"})
    @PostMapping("/add")
    public String add(@Valid @RequestBody AddUserRequest request) {
        if (!userRepo.findByEmail(request.email).isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists");
        }
        String encodedPassword = passwordEncoder.encode(request.rawPassword);

        ERole role;

        try {
            role = ERole.fromString(request.role);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, ERole.VALIDATION_MESSAGE);
        }

        var user = new User(
                request.name,
                request.email,
                encodedPassword,
                role
        );
        request.image.ifPresent(user::setImage);

        userRepo.save(user);

        return user.getId();
    }

    boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication
                .getAuthorities()
                .contains(new SimpleGrantedAuthority("SCOPE_users:admin"));
    }
}
