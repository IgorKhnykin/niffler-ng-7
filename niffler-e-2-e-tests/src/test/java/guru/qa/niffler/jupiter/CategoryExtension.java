package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.TestData;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Random;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    private final SpendApiClient client = new SpendApiClient();

    public final static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        String randomCategoryName = "randomCategory" + new Random().nextInt(1_000_000);

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                            String categoryTestName = anno.categoryName().isBlank() ? randomCategoryName : anno.categoryName();
                            CategoryJson categoryJson = new CategoryJson(
                                    null,
                                    categoryTestName,
                                    anno.username(),
                                    false
                            );
                            CategoryJson createdCategory = client.createCategory(categoryJson);
                            if (anno.archived()) {
                                CategoryJson archivedCategoryJson = new CategoryJson(
                                        createdCategory.id(),
                                        createdCategory.name(),
                                        anno.username(),
                                        true
                                );
                                createdCategory = client.updateCategory(archivedCategoryJson);
                            }
                            context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);
                        }
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson categoryJsonFromContext = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);

                    CategoryJson archivedCategoryJson = new CategoryJson(categoryJsonFromContext.id(),
                            categoryJsonFromContext.name(),
                            categoryJsonFromContext.username(),
                            true);
                    client.updateCategory(archivedCategoryJson);
                });
    }
}
