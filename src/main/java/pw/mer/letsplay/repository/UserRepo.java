package pw.mer.letsplay.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pw.mer.letsplay.model.User;

public interface UserRepo extends MongoRepository<User, String> {
}
