package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;
@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient{

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl());
        this.authApi = retrofit.create(AuthApi.class);
    }

    public @Nonnull String getToken() {
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
        return Objects.requireNonNull(jsonResponse.get("access_token").asText());
    }

    public void register(String username, String password) {
        final Response<Void> response;
        try {
            authApi.requestRegisterForm().execute();

            response = authApi.register(ThreadSafeCookieStore.INSTANCE.xsrfCookieValue(), username, password, password)
                    .execute();

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(201, response.code(), "Не удалось зарегистрироваться");
    }


}
