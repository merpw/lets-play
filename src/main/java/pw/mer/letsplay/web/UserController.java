package pw.mer.letsplay.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasAuthority('SCOPE_users')")
public class UserController {
    private final UserRepo userRepo;

    @Autowired
    public UserController(UserRepo productRepo) {
        this.userRepo = productRepo;
    }

    @GetMapping
    public List<User> index() {
        return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable String id) {
        return userRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        if (userRepo.findById(id).isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }
        userRepo.deleteById(id);
    }

    public static class UpdateUserRequest {
        @Nullable
        public String name;
        @Nullable
        public String email;
        @Nullable
        @JsonProperty("password")
        public String rawPassword;
        @Nullable
        public ERole role;
    }

    PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/{id}")
    public void updateById(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        var user = userRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "User not found"));

        if (request.email != null && !request.email.equals(user.getEmail())) {
            if (!userRepo.findByEmail(request.email).isEmpty()) {
                throw new ResponseStatusException(NOT_FOUND, "User with this email already exists");
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

    public static class AddUserRequest {
        public String name;
        public String email;
        @JsonProperty("password")
        public String rawPassword;
        public ERole role;
    }

    @PostMapping("/add")
    public String add(@RequestBody AddUserRequest request) {
        if (!userRepo.findByEmail(request.email).isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "User with this email already exists");
        }
        String encodedPassword = passwordEncoder.encode(request.rawPassword);
        var user = new User(
                request.name,
                request.email,
                encodedPassword,
                request.role
        );
        userRepo.save(user);

        return user.getId();
    }
}
