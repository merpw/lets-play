package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.shared.SharedAuthFactory;
import pw.mer.shared.SharedAbstractControllerTestsWithDB;

import java.util.List;

public abstract class AbstractControllerTests extends SharedAbstractControllerTestsWithDB {
    @Autowired
    private ProductRepo productRepo;

    public String getAdminToken() {
        var admin = new SharedAuthFactory.TestUser(List.of("products:admin"));
        return getAccessToken(admin);
    }

    public String getSellerToken() {
        var admin = new SharedAuthFactory.TestUser(List.of("products:write"));
        return getAccessToken(admin);
    }

    @Override
    public void clean() {
        productRepo.deleteAll();
    }
}
