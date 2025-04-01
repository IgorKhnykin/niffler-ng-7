package guru.qa.niffler.test.db;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UserDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Disabled
public class JbdcTest {
    UserClient userClient = new UserDbClient();
    SpendClient spendClient = new SpendDbClient();
    
    @Test
    void createAndDeleteUserViaRepository() {
        UserJson userJson = userClient.createUser("TestSpringUser", "12345");
        userClient.deleteUser(userJson.username());
    }

    @Test
    void createAndUpdateUserViaRepository() {
        UserJson userJson = userClient.createUser("TestSpringUser3", "12345");
        UserJson userJsonUpdated = userClient.updateUser(userJson);
        userClient.deleteUser(userJsonUpdated.username());
    }

    @Test
    void updateAuthUserViaRepository() {
        UserJson userJson = userClient.createUser("TestSpringUser3", "12345");
        userClient.updateAuthUser(userJson.username());
        userClient.deleteUser(userJson.username());
    }

    @Test
    void spendCreateFindDelete() {
        SpendJson response = spendClient.createSpend(new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                        null,
                        "test-cat-name-12",
                        "igorKhn",
                        false),
                CurrencyValues.EUR,
                2202222.0,
                "test spend",
                "igorKhn"
        ));

        SpendJson foundSpend = spendClient.findSpend(response);
        spendClient.deleteSpend(foundSpend);
    }

    @Test
    void spendFindByUsernameAndDescr() {
        List<SpendJson> foundSpend = spendClient.findSpendByUsernameAndDescription("igorKhn", "Машина edited");
        System.out.println(foundSpend);
    }

    @Test
    void categoryCreateFindDelete() {
        CategoryJson createdCategory = spendClient.createCategory(new CategoryJson(
                null,
                "TEST3",
                "igorKhn",
                false)
        );
        for (int i = 0; i < 10; i++) {
            SpendJson createdSpend = spendClient.createSpend(new SpendJson(
                    null,
                    new Date(),
                    createdCategory,
                    CurrencyValues.EUR,
                    2202222.0,
                    "test spend",
                    "igorKhn"
            ));
        }
        CategoryJson foundCategory = spendClient.findCategoryByUsernameAndCategoryName(createdCategory.username(), createdCategory.name());
        spendClient.deleteCategory(foundCategory);
    }

    @Test
    void sendFriendshipRequestTest() {
        UserJson requester = new UserJson(null,
                "Igor2",
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null,
                null,
                null);
        
        userClient.sendInvitation(requester, 1);
    }

    @Test
    void addFriendTest() {
        UserJson requester = new UserJson(null,
                "Igor2",
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null,
                null,
                null);
        
        userClient.addFriend(requester, 1);
    }

    @Test
    void test() {
        userClient.findById(UUID.fromString("7e781763-942d-45e6-b459-37d93e6cdebf"));
    }

    @Test
    void test1() {
        userClient.findAllUsers("Igor").stream()
                .filter(user -> user.username().contains("."))
                .forEach(user -> userClient.deleteUser(user.username()));
    }
}