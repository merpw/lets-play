package pw.mer.letsplay.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepo userRepo;

    @Autowired
    public UserController(UserRepo productRepo) {
        this.userRepo = productRepo;
    }

    @GetMapping
    public List<User> index() {
        return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public User show(@PathVariable String id) {
        return userRepo.findById(id).orElse(null);
    }
}
