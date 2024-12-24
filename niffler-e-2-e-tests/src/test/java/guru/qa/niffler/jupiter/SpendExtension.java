package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendExtension implements BeforeEachCallback, ParameterResolver {

    private final SpendApiClient spendApiClient = new SpendApiClient();

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Spend.class)
                .ifPresent(anno -> {
                    SpendJson spendJson = new SpendJson(
                            null,
                            new Date(),
                            new CategoryJson(
                                    null,
                                    anno.category(),
                                    anno.username(),
                                    false
                            ),
                            anno.currency(),
                            anno.amount(),
                            anno.description(),
                            anno.username()
                    );
                    SpendJson createSpend = spendApiClient.createSpend(spendJson);
                    context.getStore(NAMESPACE).put(context.getUniqueId(), createSpend);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(
                extensionContext.getUniqueId(),
                SpendJson.class);
    }
}
