package pw.mer.letsplay;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.Product;
import pw.mer.letsplay.model.User;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.letsplay.repository.UserRepo;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

public class ProductControllerTests extends LetsPlayApplicationTests {
    @Autowired
    ProductRepo productRepo;

    @Autowired
    UserRepo userRepo;


    @Test
    public void productsShouldBePublic() {
        when().get("/products").then().statusCode(200);
    }

    @Test
    public void products() {
        when().get("/products").then().statusCode(200).body("$.size()", is(0));

        var user = new User("test", "test@email.com", "password", ERole.ADMIN);
        userRepo.save(user);

        var product = new Product("Test name", "Test description", 100.0, user.getId());
        productRepo.save(product);

        when().get("/products").then()
                .statusCode(200)
                .body("[0].name", is(product.getName()),
                        "[0].description", is(product.getDescription()),
                        "[0].price", is(product.getPrice().floatValue()),
                        "[0].userId", is(user.getId())
                );
    }
}
