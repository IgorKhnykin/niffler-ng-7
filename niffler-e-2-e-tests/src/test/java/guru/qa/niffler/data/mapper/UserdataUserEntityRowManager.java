package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.rest.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserEntityRowManager implements RowMapper<UserEntity>{

    private UserdataUserEntityRowManager() {
    }

    public static final UserdataUserEntityRowManager instance = new UserdataUserEntityRowManager();

    @Override
    public @Nonnull UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        UserEntity user = new UserEntity();
        user.setId(rs.getObject("id", UUID.class));
        user.setUsername(rs.getString("username"));
        user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        user.setFirstname(rs.getString("firstname"));
        user.setSurname(rs.getString("surname"));
        user.setPhoto(rs.getBytes("photo"));
        user.setPhotoSmall(rs.getBytes("photo_small"));
        user.setFullname(rs.getString("full_name"));
        return user;
    }
}
