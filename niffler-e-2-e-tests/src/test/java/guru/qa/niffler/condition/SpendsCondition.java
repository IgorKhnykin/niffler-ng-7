package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.rest.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class SpendsCondition {

    public static WebElementsCondition spendingsRows(SpendJson... expectedSpends) {
        return new WebElementsCondition() {
            String expectedTextForPrint;

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected expected Spends given");
                }
                final List<String> expectedText = new ArrayList<>();
                boolean isPresent = true;

                List<String> actualText = elements.stream()
                        .map(WebElement::getText)
                        .toList();

                for (SpendJson expectedSpend : expectedSpends) {
                    String expected = getExpectedText(expectedSpend);
                    expectedText.add(expected);

                    if (isPresent) {
                        isPresent = actualText.contains(expected);
                    }
                }
                expectedTextForPrint = expectedText.toString();
                if (!isPresent) {
                    return rejected("Rows mismatched", actualText);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedTextForPrint;
            }
        };
    }

    private static String getExpectedText(SpendJson spend) {
        return "%s %s ₽ %s %s".formatted(
                spend.category().name(),
                spend.amount().longValue(),
                spend.description(),
                new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(spend.spendDate()));
    }
}
