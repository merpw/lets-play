package pw.mer.letsplay.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import pw.mer.letsplay.web.validators.UserValidators;

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
        @NotBlank(message = "Email is mandatory")
        @UserValidators.Email
        private String email;

        @NotBlank(message = "Password is mandatory")
        @UserValidators.Password
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

        return JwtConfig.issueToken(encoder, user);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public User profile(Authentication authentication) {
        return userRepo.findById(authentication.getName()).orElseThrow(() ->
                new ResponseStatusException(UNAUTHORIZED)
        );
    }


    @Setter
    public static class RegisterRequest {
        @NotBlank(message = "Name is mandatory")
        @UserValidators.Name
        private String name;

        @NotBlank(message = "Email is mandatory")
        @UserValidators.Email
        private String email;

        @NotBlank(message = "Password is mandatory")
        @UserValidators.Password
        private String password;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        var user = userRepo.findByEmail(request.email).stream().findFirst().orElse(null);
        if (user != null) {
            throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists");
        }

        user = new User(request.name, request.email, passwordEncoder.encode(request.password), ERole.USER);
        userRepo.save(user);

        return user.getId();
    }
}