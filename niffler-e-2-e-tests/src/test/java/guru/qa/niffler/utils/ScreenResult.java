package guru.qa.niffler.utils;

import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.extension.ScreenshotTestExtension;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import lombok.SneakyThrows;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.BooleanSupplier;

import static guru.qa.niffler.jupiter.extension.ScreenshotTestExtension.setActual;

public class ScreenResult implements BooleanSupplier {

    private final ScreenshotTest anno = TestMethodContextExtension.getContext().getRequiredTestMethod().getAnnotation(ScreenshotTest.class);

    private final BufferedImage expected;
    private final BufferedImage actual;
    private final ImageDiff diff;
    private final boolean hasDiff;

    public ScreenResult(BufferedImage expected, BufferedImage actual) {
        this.actual = actual;
        this.expected = anno.rewriteExpected() ?  actual : expected;
        this.diff = new ImageDiffer().makeDiff(this.expected, this.actual);
        this.hasDiff = new ImageDiffer().makeDiff(this.expected, this.actual).hasDiff();
    }

    @Override
    @SneakyThrows
    public boolean getAsBoolean() {
        if (anno.rewriteExpected()) {
            ImageIO.write(expected, "png", new File("niffler-e-2-e-tests/src/test/resources/" + anno.value()).getAbsoluteFile());
        }
        if (hasDiff) {
            ScreenshotTestExtension.setExpected(expected);
            setActual(actual);
            ScreenshotTestExtension.setDiff(diff.getMarkedImage());
        }
        return hasDiff;
    }
}
