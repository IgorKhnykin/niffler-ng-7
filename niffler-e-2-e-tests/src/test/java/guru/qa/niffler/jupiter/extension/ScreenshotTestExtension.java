package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.model.allure.ScreenDiff;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static javax.imageio.ImageIO.read;

public class ScreenshotTestExtension implements ParameterResolver, TestExecutionExceptionHandler{

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenshotTestExtension.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenshotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @Override
    @SneakyThrows
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Optional<ScreenshotTest> anno = AnnotationSupport.findAnnotation(extensionContext.getTestMethod(), ScreenshotTest.class);
        if (anno.isPresent()) {
            return read(new ClassPathResource(anno.get().value()).getInputStream());
        }
        return null;
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        try {
            ScreenDiff screenDiff = new ScreenDiff(
                    "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getExpected())),
                    "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getActual())),
                    "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getDiff()))
            );
            Allure.addAttachment("Screenshot diff",
                    "application/vnd.allure.image.diff",
                    mapper.writeValueAsString(screenDiff));
        } catch (IllegalArgumentException E) {

        }
        throw throwable;
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setExpected(BufferedImage expected) {
        TestMethodContextExtension.getContext().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getExpected() {
        return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestMethodContextExtension.getContext().getStore(NAMESPACE).put("actual", actual);
    }

    public static BufferedImage getActual() {
        return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestMethodContextExtension.getContext().getStore(NAMESPACE).put("diff", diff);
    }

    public static BufferedImage getDiff() {
        return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }
}
