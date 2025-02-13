package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserRepositoryJdbc implements UserRepository {

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();

    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull UserEntity createUser(UserEntity user) {  //+
        return userdataUserDao.createUser(user);
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(UUID id) {
        return userdataUserDao.findById(id);
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(String username) {  //+
        return userdataUserDao.findByUsername(username);
    }

    @Override
    public @Nonnull UserEntity updateUser(UserEntity user) { //+
        return userdataUserDao.update(user);
    }

    @Override
    public void remove(UserEntity user) {  //+
        userdataUserDao.delete(user);
    }

    @Override
    public List<UserEntity> findAll() {
        return userdataUserDao.findAll();
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) { //+
        UserEntity requesterEntity = userdataUserDao.findByUsername(requester.getUsername()).get();
        UserEntity addresseeEntity = userdataUserDao.findByUsername(addressee.getUsername()).get();
        userdataUserDao.sendFriendshipRequest(requesterEntity, addresseeEntity);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) { //+
        UserEntity requesterEntity = userdataUserDao.findByUsername(requester.getUsername()).get();
        UserEntity addresseeEntity = userdataUserDao.findByUsername(addressee.getUsername()).get();
        userdataUserDao.createFriendship(requesterEntity, addresseeEntity);
        userdataUserDao.createFriendship(addresseeEntity, requesterEntity);
    }
}