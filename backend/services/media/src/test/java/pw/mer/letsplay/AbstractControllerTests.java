package pw.mer.letsplay;

import pw.mer.shared.SharedAbstractControllerTestsWithDB;
import pw.mer.shared.SharedAuthFactory;

import java.util.List;

public abstract class AbstractControllerTests extends SharedAbstractControllerTestsWithDB {
    @Override
    public String getAdminToken() {
        var admin = new SharedAuthFactory.TestUser(List.of("media:write"));
        return getAccessToken(admin);
    }

    // TODO: maybe clean test media directory
}
