package guru.qa.niffler.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class DataSources {

    private DataSources() {
    }

    private static final Map<String, DataSource> datasources = new ConcurrentHashMap<>();

    public @Nonnull static DataSource dataSource(String jdbcUrl) {
        return datasources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    final String URL = StringUtils.substringAfter(key, "5432/");
                    dsBean.setUniqueResourceName(URL);
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.setProperty("user", "postgres");
                    props.setProperty("password", "secret");
                    props.setProperty("URL", key);
                    dsBean.setXaProperties(props);
                    dsBean.setPoolSize(3);
                    dsBean.setPoolSize(10);
                    P6DataSource p6DataSource = new P6DataSource(dsBean);
                    try {
                        InitialContext initialContext = new InitialContext();
                        initialContext.bind("java:comp/env/jdbc/" + URL, p6DataSource);
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                    return p6DataSource;
                }
        );
    }
}
