package pw.mer.letsplay.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.UserRepo;

import pw.mer.letsplay.web.AuthController.RegisterRequest;

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

    @PostMapping("/add-admin")
    public void addAdmin(@RequestBody RegisterRequest registerRequest) {
        var user = new User(registerRequest.name, registerRequest.email, registerRequest.password, ERole.ADMIN);
        userRepo.save(user);
    }
}
