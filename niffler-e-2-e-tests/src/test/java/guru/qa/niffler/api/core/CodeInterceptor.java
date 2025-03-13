package guru.qa.niffler.api.core;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class CodeInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());

        Response priorResponse = response.priorResponse();
        if (priorResponse != null && priorResponse.isRedirect()) {
            String location = priorResponse.header("Location");
            if (location != null && location.contains("code=")) {
                ApiLoginExtension.setCode(
                        StringUtils.substringAfter(location, "code=")
                );
            }
        }
        return response;
    }
}
