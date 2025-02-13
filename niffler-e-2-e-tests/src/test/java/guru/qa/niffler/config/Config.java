package guru.qa.niffler.config;

public interface Config {

    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env")) ? DockerConfig.INSTANCE :
                LocalConfig.INSTANCE;
    }

    String frontUrl();

    String authUrl();

    String gatewayUrl();

    String userdataUrl();

    String spendUrl();

    String ghUrl();

    String authJdbcUrl();

    String spendJdbcUrl();

    String userdataJdbcUrl();

    String currencyJdbcUrl();


}
