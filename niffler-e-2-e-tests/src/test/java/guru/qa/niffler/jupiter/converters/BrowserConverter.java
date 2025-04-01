package guru.qa.niffler.jupiter.converters;

import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static guru.qa.niffler.jupiter.converters.Browser.getConfigByBrowserName;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!context.getParameter().getType().isAssignableFrom(SelenideDriver.class)) {
            throw new ArgumentConversionException("Can only convert SelenideDriver.class");
        }
        return new SelenideDriver(getConfigByBrowserName((Browser) source));
    }
}
