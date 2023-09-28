package pw.mer.letsplay.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pw.mer.letsplay.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    List<User> findByEmail(String email);
}
