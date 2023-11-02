package pw.mer.letsplay.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;
import pw.mer.letsplay.web.validators.UserValidators;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasAuthority('SCOPE_users')")
public class UserController {
    private final UserRepo userRepo;

    private static final String NOT_FOUND_MESSAGE = "User not found";

    @Autowired
    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<User> index() {
        return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable String id) {
        return userRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        if (userRepo.findById(id).isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE);
        }
        userRepo.deleteById(id);
    }

    @Setter
    public static class UpdateUserRequest {
        @Nullable
        @UserValidators.Name
        private String name;

        @Nullable
        @UserValidators.Email
        private String email;

        @Nullable
        @JsonProperty("password")
        @UserValidators.Password
        private String rawPassword;

        private ERole role;
    }

    PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
        @NotBlank(message = "Name is mandatory")
        @UserValidators.Name
        private String name;

        @NotBlank(message = "Email is mandatory")
        @UserValidators.Email
        private String email;

        @JsonProperty("password")
        @NotBlank(message = "Password is mandatory")
        @UserValidators.Password
        private String rawPassword;

        @NotNull(message = "Role is mandatory")
        @ERole.Valid
        private String role;
    }

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
            // Should be caught by validation, but just in case:
            throw new ResponseStatusException(BAD_REQUEST);
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
