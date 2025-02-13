package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class SpendEntityListExtractor implements ResultSetExtractor<List<SpendEntity>> {
    public static SpendEntityListExtractor instance = new SpendEntityListExtractor();

    @Override
    public @Nonnull List<SpendEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, SpendEntity> spendMap = new ConcurrentHashMap<>();
        List<SpendEntity> spendEntityList = new ArrayList<>();
        UUID id;
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
            spendEntityList.add(se);
        }
    return spendEntityList;
    }
}
