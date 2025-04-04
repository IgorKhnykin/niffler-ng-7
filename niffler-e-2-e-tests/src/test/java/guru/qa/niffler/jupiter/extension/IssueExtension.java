package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.DisableByIssue;
import guru.qa.niffler.service.impl.GhApiClient;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;

public class IssueExtension implements ExecutionCondition {

    private final GhApiClient client = new GhApiClient();

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), DisableByIssue.class)
                .or(() -> AnnotationSupport.findAnnotation(
                        context.getRequiredTestClass(),
                        DisableByIssue.class,
                        SearchOption.INCLUDE_ENCLOSING_CLASSES))
                .map(byIssue -> client.getIssueState(byIssue.value()).equals("closed")
                            ? ConditionEvaluationResult.enabled("Issue closed")
                            : ConditionEvaluationResult.disabled("Disabled by issue" + byIssue.value()))
                .orElseGet((() -> ConditionEvaluationResult.enabled("Annotation @DisableByIssue not found")));
    }
}
