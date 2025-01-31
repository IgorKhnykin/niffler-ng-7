package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpendEntityExtractor implements ResultSetExtractor<SpendEntity> {
    public static SpendEntityExtractor instance = new SpendEntityExtractor();

    @Override
    public SpendEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, SpendEntity> spendMap = new ConcurrentHashMap<>();
        UUID id = null;
        while (rs.next()) {
            id = rs.getObject("id", UUID.class);
            SpendEntity se = spendMap.computeIfAbsent(id, uuid -> {
                try {
                    return SpendEntityRowMapper.instance.mapRow(rs, 1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("category_id", UUID.class));
            ce.setName(rs.getString("name"));
            ce.setUsername(rs.getString("username"));
            ce.setArchived(rs.getBoolean("archived"));
            se.setCategory(ce);
        }
        if (spendMap.isEmpty()) {
            return null;
        }
        return spendMap.get(id);
    }
}
