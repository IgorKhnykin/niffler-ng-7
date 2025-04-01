package guru.qa.niffler.api.core;

import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class RestClient {

    private final OkHttpClient okHttpClient;

    protected final Retrofit retrofit;

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }

    public RestClient(String baseUrl, boolean followRedirects) {
        this(baseUrl, followRedirects, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }

    public RestClient(String baseUrl, boolean followRedirects, @Nonnull Interceptor... interceptors) {
        this(baseUrl, followRedirects, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY, interceptors);
    }

    public RestClient(String baseUrl, boolean followRedirects, Converter.Factory converterFactory, HttpLoggingInterceptor.Level level, @Nonnull Interceptor... interceptors) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .followRedirects(followRedirects);

        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                okBuilder.addInterceptor(interceptor);
            }
        }
        okBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        okBuilder.addInterceptor(new AllureOkHttp3()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl"));

        okBuilder.cookieJar(new JavaNetCookieJar(
                new CookieManager(ThreadSafeCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL)));

        this.okHttpClient = okBuilder.build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build();
    }

    public <T> T create(Class<T> clazz) {
        return this.retrofit.create(clazz);
    }

    public static final class EmptyRestClient extends RestClient{

        public EmptyRestClient(String baseUrl) {
            super(baseUrl);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirects) {
            super(baseUrl, followRedirects);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirects, @Nonnull Interceptor... interceptors) {
            super(baseUrl, followRedirects, interceptors);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirects, Converter.Factory converterFactory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
            super(baseUrl, followRedirects, converterFactory, level, interceptors);
        }
    }
}
