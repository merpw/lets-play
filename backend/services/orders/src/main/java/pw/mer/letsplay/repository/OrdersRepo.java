package pw.mer.letsplay.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pw.mer.letsplay.model.Order;

import java.util.List;

public interface OrdersRepo extends MongoRepository<Order, String> {
    List<Order> findAllBySeller(String seller);

    List<Order> findAllByBuyer(String buyer);
}
