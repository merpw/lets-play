package pw.mer.shared.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.List;

@Configuration
public class SharedJwtConfig {
    @Value("${security.jwt.secret}")
    private String secret;

    @Bean
    SecretKey secretKey() {
        byte[] encodedSecret = new BCryptPasswordEncoder().encode(secret).getBytes();
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
