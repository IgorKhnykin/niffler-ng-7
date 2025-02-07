package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendDbClient spendDbClient = new SpendDbClient();


    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> ArrayUtils.isNotEmpty(anno.spendings()))
                .ifPresent(userAnno -> {
                    UserJson user = context.getStore(UserExtension.NAMESPACE)
                            .get(context.getUniqueId(), UserJson.class);

                    final String userName = user != null ? user.username() : userAnno.username();

                    final List<SpendJson> createdSpendsList = new ArrayList<>();

                    for (Spending spending : userAnno.spendings()) {
                        CategoryJson cj = new CategoryJson(
                                null,
                                spending.category(),
                                userName,
                                false);

                        SpendJson sj = new SpendJson(
                                null,
                                new Date(),
                                cj,
                                spending.currency(),
                                spending.amount(),
                                spending.description(),
                                userName
                        );
                        createdSpendsList.add(spendDbClient.createSpend(sj));
                    }
                    if (user != null) {
                        user.testData().spends().addAll(createdSpendsList);
                    } else {
                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                createdSpendsList
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (SpendJson[]) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class)
                .stream()
                .toArray(SpendJson[]::new);
    }
}
