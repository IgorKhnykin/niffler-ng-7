package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class NonStaticBrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private final ThreadLocal<List<SelenideDriver>> threadSafeDrivers = ThreadLocal.withInitial(ArrayList::new);

    public List<SelenideDriver> getDrivers() {
        return threadSafeDrivers.get();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        for (SelenideDriver driver : threadSafeDrivers.get()) {
            if (driver.hasWebDriverStarted()) {
                driver.close();
            }
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private void doScreenshot() {
        for (SelenideDriver driver : threadSafeDrivers.get()) {
            if (driver.hasWebDriverStarted()) {
                Allure.addAttachment(
                        "Screen on fail for browser " + driver.getSessionId(),
                        new ByteArrayInputStream(
                                ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                        )
                );
            }
        }
    }
}
