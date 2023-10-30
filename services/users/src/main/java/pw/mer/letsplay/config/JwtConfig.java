package pw.mer.letsplay.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pw.mer.letsplay.model.User;
import pw.mer.shared.config.SharedJwtConfig;

import java.util.List;

@Configuration
public class JwtConfig extends SharedJwtConfig {
    @AllArgsConstructor
    public static class JwtUser implements IJwtUser {
        private User user;

        @Override
        public String getId() {
            return user.getId();
        }

        @Override
        public List<String> getScopes() {
            return user.getRole().getScopes();
        }
    }
}
