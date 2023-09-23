package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pw.mer.letsplay.model.Product;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.letsplay.repository.UserRepo;

import java.util.List;

@RestController
public class UserController {
    private final UserRepo userRepo;

    @Autowired
    public UserController(UserRepo productRepo) {
        this.userRepo = productRepo;
    }

    @GetMapping("/users")
    public List<User> index() {
        return userRepo.findAll();
    }

    @GetMapping("/users/{id}")
    public User show(@PathVariable String id) {
        return userRepo.findById(id).orElse(null);
    }
}
