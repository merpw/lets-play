package pw.mer.letsplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.*;

/**
 * @see <a href=https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html>JWT and Spring Security</a>
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/error").permitAll()

                        .requestMatchers(GET, "/orders/**").authenticated()
                        .requestMatchers(POST, "/orders/add").authenticated()

                        .requestMatchers(PUT, "/orders/**").hasAnyAuthority("SCOPE_orders:write", "SCOPE_orders:admin")

                        .requestMatchers(DELETE, "/orders/**").hasAnyAuthority("SCOPE_orders:admin")

                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                                jwt -> jwt.decoder(jwtDecoder)
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}