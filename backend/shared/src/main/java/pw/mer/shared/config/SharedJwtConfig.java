package pw.mer.shared.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Configuration
public class SharedJwtConfig {
    @Value("${security.jwt.secret}")
    private String secret;

    private static final Logger logger = LoggerFactory.getLogger(SharedJwtConfig.class);

    @Bean
    SecretKey secretKey() {
        byte[] encodedSecret = Base64.getEncoder().encode(secret.getBytes());

        if (encodedSecret.length < 32) {
            logger.warn("JWT_SECRET is weak. It should be at least 32 bytes length");

            byte[] prolongedSecret = new byte[32];

            // UnsafeSecret -> UnsafeSecretUnsafeSecret...
            System.arraycopy(encodedSecret, 0, prolongedSecret, 0, encodedSecret.length);
            System.arraycopy(encodedSecret, 0, prolongedSecret, encodedSecret.length, 32 - encodedSecret.length);

            encodedSecret = prolongedSecret;
        }

        return new SecretKeySpec(encodedSecret, "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    public interface IJwtUser {
        String getId();

        List<String> getScopes();
    }

    public static String issueToken(JwtEncoder encoder, IJwtUser user) {
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
                                "scope", String.join(" ", user.getScopes())
                        )
                )
                .build();

        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
