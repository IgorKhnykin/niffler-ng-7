package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UserApiClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(1)
public class InitialTest {

    @Test
    void checkNoUsersExistsTest() {
        final UserApiClient userApiClient = new UserApiClient();
        List<UserJson> users = userApiClient.findAllUsers();
        Assertions.assertEquals(0, users.size());
    }
}
