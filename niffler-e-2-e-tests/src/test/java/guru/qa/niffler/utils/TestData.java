package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class TestData {
    public static final String usernameMain = "igorKhn";
    public static final String passwordMain = "1234";
    public static final String randomUsername = Faker.instance().name().username();
}
