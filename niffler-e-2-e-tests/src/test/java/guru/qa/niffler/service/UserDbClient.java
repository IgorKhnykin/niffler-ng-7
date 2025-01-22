package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDbClient {

    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoSpringJdbc();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.userdataJdbcUrl());

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());

    private final TransactionTemplate txTemplate = new TransactionTemplate(new JdbcTransactionManager(DataSources.dataSource(CFG.authJdbcUrl())));

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();


    public UserJson createUser(UserJson user) {
        return jdbcTxTemplate.execute(() -> {
            UserEntity ue = userdataUserDao.createUser(UserEntity.fromJson(user));
            return UserJson.fromEntity(ue);
        });
    }

    public Optional<UserJson> findUserById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
            Optional<UserEntity> se = userdataUserDao.findById(id);
            return se.map(UserJson::fromEntity);
        });
    }

    public List<UserJson> findAllByUsername(String username) {
        return jdbcTxTemplate.execute(() -> userdataUserDao.findByUsername(username)
                .stream()
                .map(UserJson::fromEntity)
                .toList());
    }

    public void deleteUser(UserJson UserJson) {
        jdbcTxTemplate.execute(() -> {
            UserEntity spendEntity = UserEntity.fromJson(UserJson);
            userdataUserDao.delete(spendEntity);
            return null;
        });
    }

    public List<UserJson> findAll() {
        return userdataUserDao.findAll().stream().map(UserJson::fromEntity).toList();
    }

    public UserJson createUserSpringJdbc(UserJson user) { //todo c 60 минуты посмотреть как проверить
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = new AuthUserEntity();
            authUserEntity.setUsername(user.username());
            authUserEntity.setPassword(pe.encode("1234"));
            authUserEntity.setEnabled(true);
            authUserEntity.setAccountNonExpired(true);
            authUserEntity.setAccountNonLocked(true);
            authUserEntity.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUser = authUserDaoJdbc.create(authUserEntity);

            AuthAuthorityEntity[] authAuthorityEntities = Arrays.stream(Authority.values()).map(e -> {
                AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
                authorityEntity.setUser(createdAuthUser);
                authorityEntity.setAuthority(e);
                return authorityEntity;
            }).toArray(AuthAuthorityEntity[]::new);

            authAuthorityDao.addAuthority(authAuthorityEntities);
            return UserJson.fromEntity(userdataUserDao.createUser(UserEntity.fromJson(user)));
        });

    }

    public void deleteUserSpringJdbc(UserJson userJson) {
        xaTransactionTemplate.execute(() -> {
            userdataUserDao.delete(userdataUserDao.findByUsername(userJson.username()).get());
            AuthUserEntity authUser = authUserDaoJdbc.findUserByUsername(userJson.username()).get();
            authAuthorityDao.deleteAuthority(authUser);
            authUserDaoJdbc.deleteUser(authUser);

            return null;
        });

    }
}
