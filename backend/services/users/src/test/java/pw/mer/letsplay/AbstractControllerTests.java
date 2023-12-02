package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import pw.mer.letsplay.repository.UserRepo;
import pw.mer.shared.SharedAbstractControllerTestsWithDB;
import pw.mer.shared.config.SharedJwtConfig;

public abstract class AbstractControllerTests extends SharedAbstractControllerTestsWithDB {

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

    /**
     * @deprecated use {@link #getAccessToken(String email, String password)} instead
     */
    @Deprecated
    public String getAccessToken(SharedJwtConfig.IJwtUser user) {
        throw new UnsupportedOperationException();
    }

    public String getAccessToken(String email, String password) {
        return AuthFactory.getAccessToken(email, password);
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
