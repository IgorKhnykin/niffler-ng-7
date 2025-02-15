package guru.qa.niffler.api.core;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {

    INSTANCE;

    private final ThreadLocal<CookieStore> threadLocalStore = ThreadLocal.withInitial(this::inMemoryCookieStore);

    private CookieStore inMemoryCookieStore() {
        return new CookieManager().getCookieStore();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        threadLocalStore.get().add(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return threadLocalStore.get().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return threadLocalStore.get().getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return threadLocalStore.get().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return threadLocalStore.get().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return threadLocalStore.get().removeAll();
    }

    public String cookieValue(String name) {
        return threadLocalStore.get()
                .getCookies()
                .stream()
                .filter(c -> c.getName().equals(name))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow();
    }

    public String xsrfCookieValue() {
        return threadLocalStore.get()
                .getCookies()
                .stream()
                .filter(c -> c.getName().equals("XSRF-TOKEN"))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow();
    }
}
