package pw.mer.shared;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import pw.mer.shared.config.SharedJwtConfig;

/**
 * A base class for all controller tests.
 * <p>
 * It configures RestAssured to use the port of the server under test and provides methods to get access tokens.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SharedAbstractControllerTestsBase {
    @Autowired
    JwtEncoder jwtEncoder;

    public String getAccessToken(SharedJwtConfig.IJwtUser user) {
        return SharedJwtConfig.issueToken(jwtEncoder, user);
    }

    public abstract String getAdminToken();

    public void clean() {
    }

    public void init() {
    }

    @LocalServerPort
    private Integer port;

    @BeforeEach
    @SuppressWarnings("java:S2696")
        // setting RestAssured.port is fine as we execute tests sequentially
    void beforeEach() {
        RestAssured.port = port;

        clean();
        init();
    }
}
