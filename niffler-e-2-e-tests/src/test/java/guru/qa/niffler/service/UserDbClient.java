package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.UserRepositoryJdbc;
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

    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoSpringJdbc();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.userdataJdbcUrl());

    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());

    private final TransactionTemplate txTemplate = new TransactionTemplate(new JdbcTransactionManager(DataSources.dataSource(CFG.authJdbcUrl())));

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final UserRepository userRepository = new UserRepositoryJdbc();

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

    public UserJson createUserSpringJdbc(UserJson user) {
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
            xaTransactionTemplate.holdConnectionAfterAction();
            return UserJson.fromEntity(userdataUserDao.createUser(UserEntity.fromJson(user)));
        });
    }

    public void deleteUserSpringJdbc(UserJson userJson) {
        xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = authUserDaoJdbc.findUserByUsername(userJson.username()).get();
            userdataUserDao.delete(userdataUserDao.findByUsername(userJson.username()).get());
            authAuthorityDao.deleteAuthority(authUser);
            authUserDaoJdbc.deleteUser(authUser);

            return null;
        });
    }

    public UserJson createUserRepositoryJdbc(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = new AuthUserEntity();
            authUserEntity.setUsername(user.username());
            authUserEntity.setPassword(pe.encode("1234"));
            authUserEntity.setEnabled(true);
            authUserEntity.setAccountNonExpired(true);
            authUserEntity.setAccountNonLocked(true);
            authUserEntity.setCredentialsNonExpired(true);

            List<AuthAuthorityEntity> authAuthorityEntities = Arrays.stream(Authority.values()).map(e -> {
                AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
                authorityEntity.setUser(authUserEntity);
                authorityEntity.setAuthority(e);
                return authorityEntity;
            }).toList();

            authUserEntity.setAuthorities(authAuthorityEntities);

            AuthUserEntity userEntity =  authUserRepository.create(authUserEntity);

            authUserRepository.findUserById(userEntity.getId());

            authUserRepository.findUserByUsername(userEntity.getUsername());

            return UserJson.fromEntity(userdataUserDao.createUser(UserEntity.fromJson(user)));
        });
    }

    public void deleteUserRepositoryJdbc(UserJson userJson) {
        xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = authUserRepository.findUserByUsername(userJson.username()).get();
            authUserRepository.deleteUser(authUser);
            authUserDaoJdbc.deleteUser(authUser);
            return null;
        });
    }

    public void sendFriendship(UserJson requester, UserJson addressee) {
        jdbcTxTemplate.execute(() -> {
            UserEntity requesterEntity = UserEntity.fromJson(requester);
            UserEntity addresseeEntity = UserEntity.fromJson(addressee);
            userRepository.updateUsers(requesterEntity, addresseeEntity);
            return null;
        });
    }

    public Optional<AuthUserEntity> findUserByName(String username) {
        return authUserRepository.findUserByUsername(username);
    }

    public List<AuthUserEntity> findUAllSpring() {
        return authUserRepository.findAll();
    }
}
