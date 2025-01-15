package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDbClient {

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();

    public UserJson createUser(UserJson user) {
        UserEntity ue = userdataUserDao.createUser(UserEntity.fromJson(user));
        return UserJson.fromEntity(ue);
    }

    public Optional<UserJson> findUserById(UUID id) {
        Optional<UserEntity> se = userdataUserDao.findById(id);
        return se.map(UserJson::fromEntity);
    }

    public List<UserJson> findAllByUsername(String username) {
        return userdataUserDao.findByUsername(username)
                .stream()
                .map(UserJson::fromEntity)
                .toList();
    }

    public void deleteUser(UserJson UserJson) {
        UserEntity spendEntity = UserEntity.fromJson(UserJson);
        userdataUserDao.delete(spendEntity);
    }
}
