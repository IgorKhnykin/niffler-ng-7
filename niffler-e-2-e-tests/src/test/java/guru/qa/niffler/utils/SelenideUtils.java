package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;
import guru.qa.niffler.jupiter.converters.Browser;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class SelenideUtils {

    public static SelenideConfig getConfigByBrowserName(Browser browser) {
        SelenideConfig config = new SelenideConfig()
                .pageLoadStrategy("eager")
                .timeout(5000);

        return switch (browser) {
            case CHROME -> {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("lang=ru-RU");
                yield config.browser(browser.name().toLowerCase())
                        .browserCapabilities(options);
            }
            case FIREFOX -> {
                FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("intl.accept_languages", "ru-RU");
                FirefoxOptions options = new FirefoxOptions().setProfile(profile);
                yield config.browser(browser.name().toLowerCase())
                        .browserCapabilities(options);
            }
        };
    }
}
