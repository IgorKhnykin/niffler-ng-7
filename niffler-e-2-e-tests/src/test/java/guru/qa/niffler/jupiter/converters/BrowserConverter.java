package guru.qa.niffler.jupiter.converters;

import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static guru.qa.niffler.utils.SelenideUtils.getConfigByBrowserName;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        boolean assignableFrom = context.getParameter().getType().isAssignableFrom(SelenideDriver.class);
        Assertions.assertTrue(assignableFrom, "Can only convert SelenideDriver.class");

        return new SelenideDriver(getConfigByBrowserName((Browser) source));
    }
}
