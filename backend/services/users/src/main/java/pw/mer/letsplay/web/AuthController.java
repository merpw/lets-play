package pw.mer.letsplay.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.config.JwtConfig;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;
import pw.mer.shared.config.SharedJwtConfig;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@RestController
@RequestMapping("/auth")
public class AuthController {

    JwtEncoder encoder;

    @Autowired
    public void setEncoder(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    private UserRepo userRepo;

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Setter
    public static class LoginRequest {
        @JsonProperty("email")
        @NotBlank(message = "Email is mandatory")
        @Size(min = 3, max = 320, message = "Email is not valid")
        @Email(message = "Email is not valid")
        private String email;

        @JsonProperty("password")
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        private String password;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
        var user = userRepo.findByEmail(request.email).stream().findFirst().orElseThrow(() ->
                new ResponseStatusException(BAD_REQUEST, "Invalid email")
        );

        if (!passwordEncoder.matches(request.password, user.getEncodedPassword())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid password");
        }

        var jwtUser = new JwtConfig.JwtUser(user);
        return SharedJwtConfig.issueToken(encoder, jwtUser);
    }

    @SecurityRequirement(name = "Authentication")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public User profile(Authentication authentication) {
        return userRepo.findById(authentication.getName()).orElseThrow(() ->
                new ResponseStatusException(UNAUTHORIZED)
        );
    }


    @Setter
    public static class RegisterRequest {
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
        private String password;

        @JsonProperty("role")
        @Schema(description = "Can be user or seller. Default is user")
        private String role;

        @JsonProperty("image")
        @Schema(description = "ID of the image to use as profile picture")
        private Optional<String> image = Optional.empty();
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        var existingUser = userRepo.findByEmail(request.email).stream().findFirst();
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists");
        }

        ERole role;

        if (request.role != null) {
            try {
                role = ERole.fromString(request.role);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(BAD_REQUEST, ERole.VALIDATION_MESSAGE);
            }
            if (role == ERole.ADMIN) {
                throw new ResponseStatusException(BAD_REQUEST, "Can not register as admin");
            }
        } else {
            role = ERole.USER;
        }

        var user = new User(request.name, request.email, passwordEncoder.encode(request.password), role);

        request.image.ifPresent(user::setImage);

        userRepo.save(user);

        return user.getId();
    }
}