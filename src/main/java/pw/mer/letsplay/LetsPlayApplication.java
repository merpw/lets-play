package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.Product;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.letsplay.repository.UserRepo;

@SpringBootApplication
public class LetsPlayApplication implements CommandLineRunner {
    ProductRepo productRepo;

    @Autowired
    public void setProductRepo(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    UserRepo userRepo;

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(LetsPlayApplication.class, args);
    }

    @Override
    public void run(String... args) {
        var user = new User("user", "user@mer.pw", "password", ERole.USER);
        userRepo.save(user);
        productRepo.save(new Product("New Product", "Description", 100.0, user.getId()));
    }
}
