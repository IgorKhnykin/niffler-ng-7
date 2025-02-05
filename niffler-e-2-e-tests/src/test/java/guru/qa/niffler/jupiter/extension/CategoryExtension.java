package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    private final SpendDbClient dbClient = new SpendDbClient();

    private final SpendApiClient client = new SpendApiClient();

    public final static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> ArrayUtils.isNotEmpty(anno.category()))
                .ifPresent(userAnno -> {
                            Category anno = userAnno.category()[0];
                            String categoryTestName = anno.categoryName().isBlank() ? randomCategoryName() : anno.categoryName();
                            CategoryJson categoryJson = new CategoryJson(
                                    null,
                                    categoryTestName,
                                    userAnno.username(),
                                    anno.archived()
                            );
                            CategoryJson createdCategory = dbClient.createCategory(categoryJson);
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
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> ArrayUtils.isNotEmpty(anno.category()))
                .ifPresent(anno -> {
                    CategoryJson categoryJsonFromContext = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
                    dbClient.deleteCategory(categoryJsonFromContext);
                });
    }
}
