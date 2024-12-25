package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import java.util.Random;

public class TestData {

    public static final String usernameMain = "igorKhn";
    public static final String passwordMain = "1234";
    public static final String randomUsername = Faker.instance().name().username();
    public static final String categoryName = "randomCategory" + new Random().nextInt(1_000_000);
}
