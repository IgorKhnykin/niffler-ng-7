package guru.qa.niffler.api;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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

    public RestClient(String baseUrl, boolean followRedirects, Converter.Factory converterFactory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .followRedirects(followRedirects);

        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                okBuilder.addInterceptor(interceptor);
            }
        }
        okBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));

        okBuilder.cookieJar(new JavaNetCookieJar(
                new CookieManager(ThreadSafeCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL)));

        this.okHttpClient = okBuilder.build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build();
    }
}
