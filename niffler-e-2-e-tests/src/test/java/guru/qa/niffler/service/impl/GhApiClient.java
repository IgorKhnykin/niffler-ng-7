package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhAPI;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.GhClient;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GhApiClient implements GhClient {

    private static final String GH_TOKEN = "GH_TOKEN";

    private static final Config CFG = Config.getInstance();

    private final GhAPI client = new RestClient.EmptyRestClient(CFG.ghUrl()).create(GhAPI.class) ;


    public @Nonnull String getIssueState(String issueNumber) {
        final Response<JsonNode> response;
        try {
            response = client.getIssueStatus("Bearer " + System.getenv(GH_TOKEN), issueNumber)
                    .execute();
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить информацию по issue");
        return Objects.requireNonNull(response.body()).get("state").asText();
    }
}
