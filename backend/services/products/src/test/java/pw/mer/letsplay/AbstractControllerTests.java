package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.shared.SharedAbstractControllerTests;
import pw.mer.shared.config.SharedJwtConfig;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTests extends SharedAbstractControllerTests {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    JwtEncoder jwtEncoder;

    public String getAccessToken(SharedJwtConfig.IJwtUser user) {
        return SharedJwtConfig.issueToken(jwtEncoder, user);
    }

    private final AuthFactory.TestUser initialAdmin = new AuthFactory.TestUser(List.of("products:write"));

    public String getAdminToken() {
        return getAccessToken(initialAdmin);
    }

    @Override
    public void clean() {
        productRepo.deleteAll();
    }
}
