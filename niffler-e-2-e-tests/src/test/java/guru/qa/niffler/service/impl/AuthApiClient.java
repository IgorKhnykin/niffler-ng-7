package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.utils.OauthUtils;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import retrofit2.Response;


public class AuthApiClient extends RestClient{

    private static final Config CFG = Config.getInstance();
    private static final String REDIRECT_URI = CFG.frontUrl() + "authorized";
    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "client";
    private static final String SCOPE = "openid";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CODE_CHALLENGE_METHOD = "S256";

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    @Step("Авторизовать пользователя {username}")
    public String login(String username, String password) {
        final String codeVerifier = OauthUtils.generateCodeVerifier();
        final String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);
        ThreadSafeCookieStore.INSTANCE.removeAll();

        authApi.authorize(
                RESPONSE_TYPE,
                CLIENT_ID,
                SCOPE,
                REDIRECT_URI,
                codeChallenge,
                CODE_CHALLENGE_METHOD
        ).execute();

        authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.xsrfCookieValue()
        ).execute();

        Response<JsonNode> tokenResponse = authApi.getToken(
                ApiLoginExtension.getCode(),
                REDIRECT_URI,
                codeVerifier,
                GRANT_TYPE,
                CLIENT_ID
        ).execute();

        return "Bearer " + tokenResponse.body().get("id_token").asText();
    }
}
