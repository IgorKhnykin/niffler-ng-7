package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UserApiClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

@Isolated
public class FinalTest {

    @Test
    void checkUsersExistsTest() {
        final UserApiClient userApiClient = new UserApiClient();
        List<UserJson> users = userApiClient.findAllUsers();
        Assertions.assertFalse(users.isEmpty());
    }
}
