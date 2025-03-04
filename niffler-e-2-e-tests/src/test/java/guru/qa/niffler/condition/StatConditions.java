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
public class StatConditions {

    public static WebElementsCondition statBubbles(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            boolean isRgbaEquals = true;
            boolean isTextEquals = true;

            private final String expectedRgba = Arrays.stream(expectedBubbles)
                    .map(bubble -> bubble.color().rgba)
                    .toList()
                    .toString();

            private final String expectedText = Arrays.stream(expectedBubbles)
                    .map(Bubble::text)
                    .toList()
                    .toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                final List<String> actualRgbaList = new ArrayList<>();
                final List<String> actualTextList = new ArrayList<>();

                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected expected Colors given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = "List size mismatched (expected %s, actual %s)".formatted(expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                for (int i = 0; i < elements.size(); i++) {
                    final String rgbaValueActual = elements.get(i).getCssValue("background-color");
                    final String rgbaValueExpected = expectedBubbles[i].color().rgba;

                    final String textValueActual = elements.get(i).getText();
                    final String textValueExpected = expectedBubbles[i].text();

                    actualRgbaList.add(rgbaValueActual);
                    actualTextList.add(textValueActual);

                    if (isTextEquals) {
                        isTextEquals = textValueActual.equals(textValueExpected);
                    }

                    if (isRgbaEquals) {
                        isRgbaEquals = rgbaValueActual.equals(rgbaValueExpected);
                    }
                }
                if (!isRgbaEquals) {
                    String actualRgba = actualRgbaList.toString();
                    return rejected("List expected Colors mismatched", actualRgba);
                }
                if (!isTextEquals) {
                    String actualText = actualTextList.toString();
                    return rejected("List expected Texts mismatched", actualText);
                }
                return accepted();
            }

            @Override
            public String toString() {
                if (!isRgbaEquals) {
                    return expectedRgba;
                } else if (!isTextEquals) {
                    return expectedText;
                }
                return "";
            }
        };
    }

    public static WebElementsCondition statBubblesAnyOrder(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            boolean containsRgba = true;
            boolean containsText = true;

            private final String expectedRgba = Arrays.stream(expectedBubbles)
                    .map(bubble -> bubble.color().rgba)
                    .toList()
                    .toString();

            private final String expectedText = Arrays.stream(expectedBubbles)
                    .map(Bubble::text)
                    .toList()
                    .toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                final List<String> rgbaActualValues = elements.stream()
                        .map(el -> el.getCssValue("background-color"))
                        .toList();

                final List<String> rgbaActualText = elements.stream()
                        .map(WebElement::getText)
                        .toList();

                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected expected Colors given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = "List size mismatched (expected %s, actual %s)".formatted(expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }
                for (int i = 0; i < elements.size(); i++) {
                    if (containsRgba) {
                        containsRgba = rgbaActualValues.contains(expectedBubbles[i].color().rgba);
                    }
                    if (containsText) {
                        containsText = rgbaActualText.contains(expectedBubbles[i].text());
                    }
                }
                if (!containsRgba) {
                    String actualRgba = rgbaActualValues.toString();
                    return rejected("List expected Colors mismatched", actualRgba);
                }
                if (!containsText) {
                    String actualText = rgbaActualText.toString();
                    return rejected("List expected Texts mismatched", actualText);
                }
                return accepted();
            }

            @Override
            public String toString() {
                if (!containsRgba) {
                    return expectedRgba;
                } else if (!containsText) {
                    return expectedText;
                }
                return "";
            }
        };
    }

    public static WebElementsCondition statBubblesContains(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            boolean containsRgba = true;
            boolean containsText = true;

            private final String expectedRgba = Arrays.stream(expectedBubbles)
                    .map(bubble -> bubble.color().rgba)
                    .toList()
                    .toString();

            private final String expectedText = Arrays.stream(expectedBubbles)
                    .map(Bubble::text)
                    .toList()
                    .toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                final List<String> rgbaActualValues = elements.stream()
                        .map(el -> el.getCssValue("background-color"))
                        .toList();

                final List<String> rgbaActualText = elements.stream()
                        .map(WebElement::getText)
                        .toList();

                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected expected Colors given");
                }
                for (Bubble expectedBubble : expectedBubbles) {
                    if (containsRgba) {
                        containsRgba = rgbaActualValues.contains(expectedBubble.color().rgba);
                    }
                    if (containsText) {
                        containsText = rgbaActualText.contains(expectedBubble.text());
                    }
                }
                if (!containsRgba) {
                    String actualRgba = rgbaActualValues.toString();
                    return rejected("List expected Colors mismatched", actualRgba);
                }
                if (!containsText) {
                    String actualText = rgbaActualText.toString();
                    return rejected("List expected Texts mismatched", actualText);
                }
                return accepted();
            }

            @Override
            public String toString() {
                if (!containsRgba) {
                    return expectedRgba;
                } else if (!containsText) {
                    return expectedText;
                }
                return "";
            }
        };
    }
}
