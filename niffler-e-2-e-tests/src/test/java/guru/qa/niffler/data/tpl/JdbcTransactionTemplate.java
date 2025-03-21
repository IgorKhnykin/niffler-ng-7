package guru.qa.niffler.data.tpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class JdbcTransactionTemplate {

    private final JdbcConnectionHolder holder;

    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public JdbcTransactionTemplate(String jdbcUrl) {
        this.holder = Connections.holder(jdbcUrl);
    }

    public JdbcTransactionTemplate holdConnectionAfterAction() {
        closeAfterAction.set(false);
        return this;
    }

    public @Nullable <T> T execute(Supplier<T> function, int transactionLevel) {
        Connection connection = null;
        try {
            connection = holder.connection();
            connection.setTransactionIsolation(transactionLevel);
            connection.setAutoCommit(false);
            T result = function.get();
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (SQLException e) {
            if (connection != null)
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            throw new RuntimeException(e);
        }
        finally {
            if (closeAfterAction.get()) {
                holder.close();
            }
        }
    }

    public @Nullable <T> T execute(Supplier<T> function) {
        return execute(function, Connection.TRANSACTION_READ_COMMITTED);
    }
}
