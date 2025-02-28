package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    private final SpendClient client = SpendClient.getInstance();

    public final static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                            if (ArrayUtils.isNotEmpty(userAnno.categories())) {
                                UserJson user = context.getStore(UserExtension.NAMESPACE).get(context.getUniqueId(), UserJson.class);

                                final String username = user != null ? user.username() : userAnno.username();

                                List<CategoryJson> categories = new ArrayList<>();

                                for (Category category : userAnno.categories()) {
                                    String categoryTestName = category.categoryName().isBlank() ? randomCategoryName() : category.categoryName();
                                    CategoryJson categoryJson = new CategoryJson(
                                            null,
                                            categoryTestName,
                                            username,
                                            category.archived()
                                    );
                                    CategoryJson createdCategory = client.createCategory(categoryJson);
                                    categories.add(createdCategory);
                                }
                                if (user != null) {
                                    user.testData().categories().addAll(categories);
                                } else {
                                    context.getStore(NAMESPACE).put(context.getUniqueId(), categories);
                                }
                            }

                        }
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return (CategoryJson[]) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class).toArray(CategoryJson[]::new);
    }
}
