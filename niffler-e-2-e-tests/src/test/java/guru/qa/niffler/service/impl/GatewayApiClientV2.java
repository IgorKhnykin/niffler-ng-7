package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.GatewayApiV2;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.RestResponsePage;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.domain.PageImpl;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GatewayApiClientV2 {

    private static final Config CFG = Config.getInstance();

    private final GatewayApiV2 gatewayApi = new RestClient.EmptyRestClient(CFG.gatewayUrl()).create(GatewayApiV2.class);

    @Step("Получить все траты пользователя пользователя")
    public @Nullable List<SpendJson> getAllSpends(String token, int page, @Nullable String sort) {
        final Response<RestResponsePage<SpendJson>> response;
        try {
            response = gatewayApi.getSpends(token, page, sort, null, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body() == null ? Collections.emptyList() : response.body().getContent();
    }

    @Step("Найти всех пользователей через API")
    public @Nonnull List<UserJson> findAllUsers(String token, int page, @Nullable String sort, @Nullable String searchQuery) {
        Response<RestResponsePage<UserJson>> response;
        try {
            response = gatewayApi.getAllUsers(token, page, sort,  searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ? Collections.emptyList() : response.body().getContent();
    }

    @Step("Найти всех друзей через API")
    public @Nonnull List<UserJson> findAllFriends(String token, int page, @Nullable String sort, @Nullable String searchQuery) {
        Response<RestResponsePage<UserJson>> response;
        try {
            response = gatewayApi.getAllFriends(token, page, sort,  searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить друзей");
        return response.body() == null ? Collections.emptyList() : response.body().getContent();
    }
}
