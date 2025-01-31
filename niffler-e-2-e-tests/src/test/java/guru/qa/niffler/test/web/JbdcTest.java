package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.AuthUserDbClient;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JbdcTest {

    @Test
    void categoryJdbcFirstTest() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();

        CategoryJson category = categoryDbClient.createCategory(new CategoryJson(
                null,
                "r21221",
                "igorKhn",
                false));

        System.out.println(categoryDbClient.findCategoryById(category.id()));

        System.out.println(categoryDbClient.findCategoryByUsernameAndCategoryName(category.username(), category.name()));

        System.out.println(categoryDbClient.findAllByUsername(category.username()));

        categoryDbClient.deleteCategory(category);
    }

    @Test
    void categoryJdbcSecondTest() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        System.out.println(categoryDbClient.findAll());
    }

    @Test
    void spendJdbcFirstTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson sj = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "test-cat-name-4",
                                "igorKhn",
                                false),
                        CurrencyValues.EUR,
                        228.0,
                        "test desc",
                        "igorKhn"
                )
        );

        System.out.println(spendDbClient.findSpendById(sj.id()));

        System.out.println(spendDbClient.findAllByUsername(sj.username()));

        spendDbClient.deleteSpend(sj);
    }

    @Test
    void spendJdbcSecondTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        System.out.println(spendDbClient.findAll());
    }

    @Test
    void authUserJdbcFirstTest() {
        AuthUserDbClient authUserDbClient = new AuthUserDbClient();

        UserDbClient userDbClient = new UserDbClient();

        UserJson userJson = new UserJson(null,
                "valentin-27",
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null);

        UserJson userFromDb = userDbClient.createUserSpringJdbc(userJson);

        authUserDbClient.findUserByUsername(userJson.username()).get();

        userDbClient.deleteUserSpringJdbc(userFromDb);
    }

    @Test
    void authUserJdbcSecondTest() {
        AuthUserDbClient authUserDbClient = new AuthUserDbClient();
        System.out.println(authUserDbClient.findAll());
    }

    @Test
    void createUserViaRepository() {
        UserJson userJson = new UserJson(null,
                "valentina-7",
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null);
        UserDbClient userDbClient = new UserDbClient();
        userDbClient.createUserRepositoryJdbc(userJson);
    }

    @Test
    void spendFromRepository() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson response = spendDbClient.createSpendRepository(new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                        null,
                        "test-cat-name-4",
                        "igorKhn",
                        false),
                CurrencyValues.EUR,
                22888888999.0,
                "test desc",
                "igorKhn"
        ));
    }

    @Test
    void sendFriendshipRequestTest() {
        UserJson requester = new UserJson(null,
                "valentin-2",
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null);

        UserJson addressee = new UserJson(null,
                "valentin-3",
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null);

        UserDbClient userDbClient = new UserDbClient();
        userDbClient.sendFriendship(requester, addressee);
    }
}
