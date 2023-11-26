package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.shared.SharedAuthFactory;
import pw.mer.shared.SharedAbstractControllerTestsWithDB;

import java.util.List;

public abstract class AbstractControllerTests extends SharedAbstractControllerTestsWithDB {
    @Autowired
    private ProductRepo productRepo;

    @Override
    public String getAdminToken() {
        var admin = new SharedAuthFactory.TestUser(List.of("products:write"));
        return getAccessToken(admin);
    }

    @Override
    public void clean() {
        productRepo.deleteAll();
    }
}
