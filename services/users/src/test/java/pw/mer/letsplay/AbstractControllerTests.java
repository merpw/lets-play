package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import pw.mer.letsplay.repository.UserRepo;
import pw.mer.shared.SharedAbstractControllerTests;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTests extends SharedAbstractControllerTests {

    @Autowired
    private UserRepo userRepo;

    protected Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    public String getAdminToken() {
        String adminEmail = env.getProperty("initial.admin.email");
        String adminPassword = env.getProperty("initial.admin.password");

        return AuthFactory.getAccessToken(adminEmail, adminPassword);
    }

    @Override
    public void clean() {
        userRepo.deleteAll();
    }

    @Autowired
    private CommandLineRunner commandLineRunner;

    @Override
    public void init() {
        try {
            commandLineRunner.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
