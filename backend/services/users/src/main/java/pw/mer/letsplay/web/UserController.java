package pw.mer.letsplay.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Nullable;
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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

        if (authentication != null && authentication
                .getAuthorities()
                .contains(new SimpleGrantedAuthority("SCOPE_users:admin"))
        ) {
            return user;
        }

        if (user.getRole() != ERole.SELLER) {
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
        @Nullable
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        private String name;

        @JsonProperty("email")
        @Nullable
        @Email(message = "Email is not valid")
        @Size(min = 3, message = "Email is not valid")
        private String email;

        @Nullable
        @JsonProperty("password")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        private String rawPassword;

        @JsonProperty("role")
        private ERole role;
    }

    PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @SecurityRequirement(name = "Authentication", scopes = {"users:admin"})
    @PutMapping("/{id}")
    public void updateById(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        var user = userRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        if (request.email != null && !request.email.equals(user.getEmail())) {
            if (!userRepo.findByEmail(request.email).isEmpty()) {
                throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists");
            }

            user.setEmail(request.email);
        }

        if (request.name != null) {
            user.setName(request.name);
        }

        if (request.role != null) {
            user.setRole(request.role);
        }

        if (request.rawPassword != null) {
            var encodedPassword = passwordEncoder.encode(request.rawPassword);
            user.setEncodedPassword(encodedPassword);
        }

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
        userRepo.save(user);

        return user.getId();
    }
}
