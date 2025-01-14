package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.service.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;
import java.util.Optional;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendApiClient spendApiClient = new SpendApiClient();

    private final SpendDbClient spendDbClient = new SpendDbClient();

    private final CategoryDbClient categoryDbClient = new CategoryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> ArrayUtils.isNotEmpty(anno.spending()))
                .ifPresent(userAnno -> {
                    Spending anno = userAnno.spending()[0];
                    Optional<CategoryJson> categoryJson = categoryDbClient.findCategoryByUsernameAndCategoryName(userAnno.username(), anno.category());

                    CategoryJson cj = categoryJson.orElseGet(() -> new CategoryJson(null, anno.category(), userAnno.username(), false));

                    SpendJson spend = new SpendJson(
                            null,
                            new Date(),
                            cj,
                            CurrencyValues.RUB,
                            anno.amount(),
                            anno.description(),
                            userAnno.username()
                    );

                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            spendDbClient.createSpend(spend)
                    );
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }
}
