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

    @Override
    public void afterSuite() throws IOException {  //todo протестить
        final AllureLifecycle lifecycle = Allure.getLifecycle();
        final String caseId = String.valueOf(UUID.randomUUID());
        lifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setTestCaseName(caseName));
        lifecycle.startTestCase(caseId);
        lifecycle.addAttachment("niffler-auth log",
                "text/html",
                ".log",
                Files.newInputStream(Path.of("./logs/niffler-auth/app.log"))
                );

        lifecycle.stopTestCase(caseId);
        lifecycle.writeTestCase(caseId);
    }
}
