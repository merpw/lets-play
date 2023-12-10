package pw.mer.shared.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Authentication",
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SharedSwaggerConfiguration {
    @Value("${docs.api-url}")
    private String apiUrl;

    @Bean
    public OpenAPI openAPI() {
        if (apiUrl == null) {
            apiUrl = "/";
        }
        Server server = new Server().url(apiUrl);
        return new OpenAPI().servers(List.of(server));
    }
}