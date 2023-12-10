package pw.mer.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(servers = {@Server(url = "/api")})
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Authentication",
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SharedSwaggerConfiguration {
}