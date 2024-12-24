package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Random;
import java.util.UUID;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    private final SpendApiClient client = new SpendApiClient();

    private final Random random = new Random();

    public final static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                            String categoryName = "randomCategory" + random.nextInt(1_000_000);
                            CategoryJson categoryJson = new CategoryJson(
                                    UUID.randomUUID(),
                                    categoryName,
                                    anno.username(),
                                    false
                            );
                            CategoryJson createdCategory = client.createCategory(categoryJson);
                            if (anno.archived()) {
                                CategoryJson archivedCategoryJson = new CategoryJson(
                                        createdCategory.id(),
                                        categoryName,
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
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson categoryJsonFromContext = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
                    if (!categoryJsonFromContext.archived()) {
                        CategoryJson archivedCategoryJson = new CategoryJson(categoryJsonFromContext.id(),
                                categoryJsonFromContext.name(),
                                categoryJsonFromContext.username(),
                                true);
                        client.updateCategory(archivedCategoryJson);
                    }

                });
    }
}
