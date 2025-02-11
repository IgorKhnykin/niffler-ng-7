package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import guru.qa.niffler.config.Config;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class AuthApiClient{

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final AuthApi authApi = retrofit.create(AuthApi.class);

    public String getToken() {
        final Response<JsonNode> response;
        try {
            response = authApi.getToken("",
                            "http://127.0.0.1:3000/authorized",
                            "",
                            "authorization_code",
                            "client")
                    .execute();

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code());
        JsonNode jsonResponse = response.body();
        return jsonResponse.get("access_token").asText();
    }

    public void register(String username, String password) {
        final Response<Void> response;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonObject = objectMapper.createObjectNode();
            jsonObject.put("username", username);
            jsonObject.put("password", password);

            response = authApi.register(jsonObject).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось зарегистрироваться");
    }
}
