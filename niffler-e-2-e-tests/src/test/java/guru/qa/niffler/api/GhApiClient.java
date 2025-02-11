package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GhApiClient {

    private static final String GH_TOKEN = "GH_TOKEN";

    private final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.ghUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final GhAPI ghAPI = retrofit.create(GhAPI.class);

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
