package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import pw.mer.letsplay.repository.OrdersRepo;
import pw.mer.shared.SharedAuthFactory;
import pw.mer.shared.SharedAbstractControllerTestsWithDB;
import pw.mer.shared.config.SharedJwtConfig;

import java.util.List;

public abstract class AbstractControllerTests extends SharedAbstractControllerTestsWithDB {
    @Autowired
    private OrdersRepo ordersRepo;

    public String getAdminToken() {
        return getAccessToken(getAdmin());
    }

    public SharedAuthFactory.TestUser getAdmin() {
        return new SharedAuthFactory.TestUser(List.of("orders:admin"));
    }

    public SharedAuthFactory.TestUser getSeller() {
        return new SharedAuthFactory.TestUser(List.of("orders:write"));
    }

    @Override
    public void clean() {
        ordersRepo.deleteAll();
    }
}
