package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;

@SpringBootApplication
public class LetsPlayApplication implements CommandLineRunner {
    UserRepo userRepo;

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Value("${initial.admin.enabled}")
    boolean initialAdminEnabled;

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (initialAdminEnabled) {
            String name = env.getProperty("initial.admin.name");
            String email = env.getProperty("initial.admin.email");
            String password = env.getProperty("initial.admin.password");

            if (userRepo.findByEmail(email).isEmpty()) {
                User admin = new User(name, email, passwordEncoder.encode(password), ERole.ADMIN);
                userRepo.save(admin);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(LetsPlayApplication.class, args);
    }
}
