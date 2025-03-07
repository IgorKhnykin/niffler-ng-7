package guru.qa.niffler.utils;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static final String usernameMain = "IgorKhn";
    public static final String passwordMain = "1234";

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomName() {
        return faker.name().name();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static String randomCategoryName() {
        return "randomCategory" + new Random().nextInt(1_000_000);
    }

    public static String randomSentence(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
