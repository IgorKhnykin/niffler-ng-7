package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension{

    public static final String caseName = "Niffler backend logs";

    private static final AllureLifecycle lifecycle = Allure.getLifecycle();

    @Override
    public void afterSuite() throws IOException {

        final String caseId = String.valueOf(UUID.randomUUID());
        lifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setTestCaseName(caseName));
        lifecycle.startTestCase(caseId);

        addAttachToAllure("auth");
        addAttachToAllure("currency");
        addAttachToAllure("gateway");
        addAttachToAllure("spend");
        addAttachToAllure("userdata");

        lifecycle.stopTestCase(caseId);
        lifecycle.writeTestCase(caseId);
    }

    private void addAttachToAllure(String serviceName) throws IOException {
        lifecycle.addAttachment("niffler-%s log".formatted(serviceName),
                "text/html",
                ".log",
                Files.newInputStream(Path.of("./logs/niffler-%s/app.log".formatted(serviceName)))
        );
    }
}
