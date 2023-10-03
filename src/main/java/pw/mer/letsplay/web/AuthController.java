package pw.mer.letsplay.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;

import java.time.Instant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


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

    public static class LoginRequest {
        public String email;
        public String password;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        var user = userRepo.findByEmail(request.email).stream().findFirst().orElse(null);
        if (user == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid email");
        }

        if (!passwordEncoder.matches(request.password, user.getEncodedPassword())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid password");
        }

        var now = Instant.now();

        long expiry = 36000L;
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getId())
                .claims((claimsBuilder) ->
                        claimsBuilder.put(
                                "scope", String.join(" ", user.getRole().getScopes())
                        )
                )
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication) {
        return authentication.getName();
    }


    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        var user = userRepo.findByEmail(request.email).stream().findFirst().orElse(null);
        if (user != null) {
            throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists");
        }

        user = new User(request.name, request.email, passwordEncoder.encode(request.password), ERole.USER);
        userRepo.save(user);
    }
}