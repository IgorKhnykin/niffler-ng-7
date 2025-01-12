package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
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
                                "test-cat-name",
                                "igorKhn",
                                false),
                        CurrencyValues.EUR,
                        100.0,
                        "test desc",
                        "igorKhn"
                )
        );
        System.out.println(sj);
    }

    @Test
    void test1() {
        SpendDbClient spendDbClient = new SpendDbClient();
        System.out.println(spendDbClient.findSpendById(UUID.fromString("5b1f1fca-1e68-4d00-94ff-e9e49bd1e3b0")));

    }

    @Test
    void test2() {
        SpendDbClient spendDbClient = new SpendDbClient();
        System.out.println(spendDbClient.findSpendById(UUID.fromString("5b1f1fca-1e68-4d00-94ff-e9e49bd1e3b0")));

    }
}
