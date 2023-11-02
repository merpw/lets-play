package pw.mer.letsplay;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import pw.mer.shared.config.SharedJwtConfig;

import java.util.List;

public class AuthFactory {
    @Getter
    public static class TestUser implements SharedJwtConfig.IJwtUser {
        private final String id;
        private final List<String> scopes;

        public TestUser() {
            this.id = RandomStringUtils.randomAlphanumeric(5);
            this.scopes = List.of();
        }

        public TestUser(List<String> scopes) {
            this.id = RandomStringUtils.randomAlphanumeric(5);
            this.scopes = scopes;
        }
    }
}
