package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension{

    public static final String caseName = "Niffler backend logs";

    private static final AllureLifecycle lifecycle = Allure.getLifecycle();

    @Override
    public void afterSuite() throws IOException {

        final String caseId = String.valueOf(UUID.randomUUID());
        lifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setTestCaseName(caseName));
        lifecycle.startTestCase(caseId);

        Arrays.stream(serviceName).forEach(name -> {
            try {
                addAttachToAllure(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

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

    private final String[] serviceName = {"auth", "currency", "gateway", "spend", "userdata"};
}
