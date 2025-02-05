package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JbdcTest {
    @Test
    void createAndDeleteUserViaRepository() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson userJson = userDbClient.createUser("TestSpringUser", "12345");
        userDbClient.deleteUser(userJson.username());
    }

    @Test
    void createAndUpdateUserViaRepository() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson userJson = userDbClient.createUser("TestSpringUser3", "12345");
        UserJson userJsonUpdated = userDbClient.updateUser(userJson);
        userDbClient.deleteUser(userJsonUpdated.username());
    }

    @Test
    void updateAuthUserViaRepository() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson userJson = userDbClient.createUser("TestSpringUser3", "12345");
        userDbClient.updateAuthUser(userJson.username());
        userDbClient.deleteUser(userJson.username());
    }

    @Test
    void spendCreateFindDelete() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson response = spendDbClient.createSpend(new SpendJson(
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

        SpendJson foundSpend = spendDbClient.findSpend(response);
        spendDbClient.deleteSpend(foundSpend);
    }

    @Test
    void spendFindByUsernameAndDescr() {
        SpendDbClient spendDbClient = new SpendDbClient();

        List<SpendJson> foundSpend = spendDbClient.findSpendByUsernameAndDescription("igorKhn", "Машина edited");
        System.out.println(foundSpend);
    }

    @Test
    void categoryCreateFindDelete() {
        SpendDbClient spendDbClient = new SpendDbClient();

        CategoryJson createdCategory = spendDbClient.createCategory(new CategoryJson(
                null,
                "TEST3",
                "igorKhn",
                false)
        );
        for (int i = 0; i < 10; i++) {
            SpendJson createdSpend = spendDbClient.createSpend(new SpendJson(
                    null,
                    new Date(),
                    createdCategory,
                    CurrencyValues.EUR,
                    2202222.0,
                    "test spend",
                    "igorKhn"
            ));
        }
        CategoryJson foundCategory = spendDbClient.findCategoryByUsernameAndCategoryName(createdCategory.username(), createdCategory.name());
        spendDbClient.deleteCategory(foundCategory);
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
                null);

        UserDbClient userDbClient = new UserDbClient();
        userDbClient.sendInvitation(requester, 1);
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
                null);

        UserDbClient userDbClient = new UserDbClient();
        userDbClient.addFriend(requester, 1);
    }

    @Test
    void test() {
        UserDbClient userDbClient = new UserDbClient();
        userDbClient.findById(UUID.fromString("7e681763-942d-45e6-b459-37d93e6cdebf"));
    }

    @Test
    void test1() {
        UserDbClient userDbClient = new UserDbClient();
        userDbClient.findAllUsers().forEach(System.out::println);
    }
}
