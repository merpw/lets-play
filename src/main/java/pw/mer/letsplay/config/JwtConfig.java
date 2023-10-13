package pw.mer.letsplay.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import pw.mer.letsplay.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Objects;

@Configuration
public class JwtConfig {
    @Value("${security.jwt.secret}")
    private String secret;

    Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    @Bean
    SecretKey secretKey() {
        if (Objects.equals(secret, "UnsafeSecret")) {
            logger.warn("Using unsafe secret for JWT. Please change it in application.properties");
        }
        byte[] encodedSecret = new BCryptPasswordEncoder().encode(secret).getBytes();
        return new SecretKeySpec(encodedSecret, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    public static String issueToken(JwtEncoder encoder, User user) {
        JwsHeader header = JwsHeader.with(() -> "HS256").build();

        var now = Instant.now();
        long expiry = 36000L;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getId())
                .claims(claimsBuilder ->
                        claimsBuilder.put(
                                "scope", String.join(" ", user.getRole().getScopes())
                        )
                )
                .build();

        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
