package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions{

    public static WebElementsCondition color(Color... expectedColors) {
        return new WebElementsCondition() {
            private final String expectedRgba = Arrays.stream(expectedColors).map(color -> color.rgba).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                final List<String> actualRgbaList = new ArrayList<>();
                boolean isEquals = true;
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected expectedColors given");
                }
                if (expectedColors.length != elements.size()) {
                    final String message = "List size mismatched (expected %s, actual %s)".formatted(expectedColors.length, elements.size());
                    return rejected(message, elements);
                }

                for (int i = 0; i < elements.size(); i++) {
                    final String rgbaValueActual = elements.get(i).getCssValue("background-color");
                    final String rgbaValueExpected = expectedColors[i].rgba;
                    actualRgbaList.add(rgbaValueActual);

                    if (isEquals) {
                        isEquals = rgbaValueActual.equals(rgbaValueExpected);
                    }
                }
                if (!isEquals) {
                    String actualRgba = actualRgbaList.toString();
                    return rejected("List expectedColors mismatched", actualRgba);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }
}
