package pw.mer.shared;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import pw.mer.shared.config.SharedJwtConfig;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class SharedAuthFactory {
    @Getter
    public static class TestUser implements SharedJwtConfig.IJwtUser {
        private final String id;
        private final List<String> scopes;

        public TestUser() {
            this.id = UUID.randomUUID().toString();
            this.scopes = List.of();
        }

        public TestUser(List<String> scopes) {
            this.id = UUID.randomUUID().toString();
            this.scopes = scopes;
        }
    }
}
