package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AutorityEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private final DataSource dataSource;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addAuthority(AuthAuthorityEntity... authorityEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.batchUpdate("INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authorityEntity[i].getUser().getId());
                        ps.setString(2, authorityEntity[i].getAuthority().name());
                    }
                    @Override
                    public int getBatchSize() {
                        return authorityEntity.length;
                    }
                });
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("SELECT * FROM authority", AutorityEntityRowMapper.instance);
    }
}
