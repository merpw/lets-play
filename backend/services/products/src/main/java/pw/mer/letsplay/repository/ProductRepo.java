package pw.mer.letsplay.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pw.mer.letsplay.model.Product;

public interface ProductRepo extends MongoRepository<Product, String> {

}
