package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static guru.qa.niffler.data.DataBases.transaction;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson user) {
        return transaction(connection -> {
            UserEntity ue = new UserdataUserDaoJdbc(connection).createUser(UserEntity.fromJson(user));
            return UserJson.fromEntity(ue);
        }, CFG.userdataJdbcUrl());
    }

    public Optional<UserJson> findUserById(UUID id) {
        return transaction(connection -> {
            Optional<UserEntity> se = new UserdataUserDaoJdbc(connection).findById(id);
            return se.map(UserJson::fromEntity);
        }, CFG.userdataJdbcUrl());
    }

    public List<UserJson> findAllByUsername(String username) {
        return transaction(connection -> {
            return new UserdataUserDaoJdbc(connection).findByUsername(username)
                    .stream()
                    .map(UserJson::fromEntity)
                    .toList();
        }, CFG.userdataJdbcUrl());
    }

    public void deleteUser(UserJson UserJson) {
        transaction(connection -> {
            UserEntity spendEntity = UserEntity.fromJson(UserJson);
            new UserdataUserDaoJdbc(connection).delete(spendEntity);
        }, CFG.userdataJdbcUrl());
    }
}
