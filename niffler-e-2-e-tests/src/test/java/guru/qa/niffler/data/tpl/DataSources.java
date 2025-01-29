package guru.qa.niffler.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {

    private DataSources() {
    }

    private static final Map<String, DataSource> datasources = new ConcurrentHashMap<>();

    public static DataSource dataSource(String jdbcUrl) {
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
                    return dsBean;
                }
        );
    }
}
