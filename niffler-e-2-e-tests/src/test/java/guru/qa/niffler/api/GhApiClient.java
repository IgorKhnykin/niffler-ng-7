package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GhApiClient extends RestClient{

    private static final String GH_TOKEN = "GH_TOKEN";

    private static final Config CFG = Config.getInstance();

    private final GhAPI ghAPI;

    public GhApiClient() {
        super(CFG.ghUrl());
        this.ghAPI = retrofit.create(GhAPI.class);
    }

    public @Nonnull String getIssueState(String issueNumber) {
        final Response<JsonNode> response;
        try {
            response = ghAPI.getIssueStatus("Bearer " + System.getenv(GH_TOKEN), issueNumber)
                    .execute();
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить информацию по issue");
        return Objects.requireNonNull(response.body()).get("state").asText();
    }
}
