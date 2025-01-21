package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.*;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class JbdcTest {

    @Test
    void test() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson sj = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "test-cat-name-2",
                                "igorKhn",
                                false),
                        CurrencyValues.EUR,
                        100.0,
                        "test desc",
                        null
                )
        );
        System.out.println(sj);
    }

    @Test
    void test1() {
        SpendDbClient spendDbClient = new SpendDbClient();
        System.out.println(spendDbClient.findSpendById(UUID.fromString("5b1fca-18-4d00-94ff-e9e49bd1e3b0")));

    }

    @Test
    void test2() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        System.out.println(categoryDbClient.findCategoryById(UUID.fromString("3b50ef22-a96c-4987-a0e7-24d133e800fc")));
    }

    @Test
    void test3() {
        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.findAllByUsername("igorKhn").forEach(System.out::println);
    }

    @Test
    void test4() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        categoryDbClient.findAllByUsername("igorKhn").forEach(System.out::println);
    }

    @Test
    void test5() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        System.out.println(categoryDbClient.findCategoryByUsernameAndCategoryName("igorKhn", "ss"));
    }

    @Test
    void test6() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson sj = spendDbClient.findSpendById(UUID.fromString("f18d9116-d0d0-11ef-8752-0242ac110004")).get();
        spendDbClient.deleteSpend(sj);
    }

    @Test
    void test7() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson uj = userDbClient.findAllByUsername("igorKhn1").get(0);
        userDbClient.deleteUser(uj);
    }

    @Test
    void test8() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        CategoryJson dbClient = categoryDbClient.findCategoryById(UUID.fromString("bdca84a8-d254-11ef-9adb-0242ac110004")).get();
        categoryDbClient.deleteCategory(dbClient);
    }

    @Test
    void test9() {
        AuthUserDbClient authUserDbClient = new AuthUserDbClient();
        AuthUserJson auth = new AuthUserJson(
                null,
                "AuthTestUser999",
                "qwert",
                true,
                true,
                true,
                true
        );

        AuthAuthorityJson authorityJson = new AuthAuthorityJson(null, auth, Authority.read);
        authUserDbClient.createUser(authorityJson);

    }

    @Test
    void test10() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson userJson = new UserJson(null,
                "valentin-3",
                null,
                 null,
                null,
                CurrencyValues.EUR,
                null,
                null);
        userDbClient.createUserSpringJdbc(userJson);
        System.out.println(userJson);
    }

    @Test
    void test11() {
        SpendDbClient spendDbClient = new SpendDbClient();
        System.out.println(spendDbClient.findAll());
    }

    @Test
    void test12() {
        UserDbClient userDbClient = new UserDbClient();
        userDbClient.findAll().forEach(System.out::println);
    }

    @Test
    void test13() {
        AuthAuthorityDbClient authAuthorityDbClient = new AuthAuthorityDbClient();
        authAuthorityDbClient.findAll().forEach(System.out::println);
    }

    @Test
    void test14() {
        UserDbClient userDbClient = new UserDbClient();
        userDbClient.findAll().forEach(System.out::println);
    }

    @Test
    void test15() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        categoryDbClient.findAll().forEach(System.out::println);
    }
}
