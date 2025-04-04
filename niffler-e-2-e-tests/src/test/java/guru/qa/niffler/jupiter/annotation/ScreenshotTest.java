package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.ScreenshotTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({ScreenshotTestExtension.class, BrowserExtension.class})
@Test
public @interface ScreenshotTest {
    String value();
    boolean rewriteExpected() default false;
}
