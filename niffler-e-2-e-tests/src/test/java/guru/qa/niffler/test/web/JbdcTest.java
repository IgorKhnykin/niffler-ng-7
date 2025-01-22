package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
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
                "r2121",
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
                                "test-cat-name-3",
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
                "valentin-8",
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null);
        UserJson userFromDb = userDbClient.createUserSpringJdbc(userJson);

        System.out.println(userJson);

        AuthUserJson authUserJson = authUserDbClient.findUserByUsername(userJson.username()).get();

        System.out.println(authUserDbClient.findUserById(authUserJson.id()));

        userDbClient.deleteUserSpringJdbc(userFromDb);
    }

    @Test
    void authUserJdbcSecondTest() {
        AuthUserDbClient authUserDbClient = new AuthUserDbClient();
        System.out.println(authUserDbClient.findAll());
    }
}
