package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface GhAPI {

    @GET("/repos/IgorKhnykin/niffler-ng-7/issues/{issue_number}")
    @Headers({
            "Accept: application/vnd.github+json",
            "X-GitHub-Api-Version: 2022-11-28"
    })
    Call<JsonNode> getIssueStatus(@Header("Authorization") String bearerToken,
                                  @Path("issue_number") String issueNumber);
}
