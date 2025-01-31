package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();

    @Override
    public void updateUsers(UserEntity requester, UserEntity addressee) {
        UserEntity requesterEntity = userdataUserDao.findByUsername(requester.getUsername()).get();
        UserEntity addresseeEntity = userdataUserDao.findByUsername(addressee.getUsername()).get();
        userdataUserDao.sendFriendshipRequest(requesterEntity, addresseeEntity);
        userdataUserDao.sendFriendshipRequest(addresseeEntity, requesterEntity);
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        return null;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void delete(UserEntity user) {

    }

    @Override
    public List<UserEntity> findAll() {
        return List.of();
    }

    @Override
    public void sendFriendshipRequest(UserEntity requester, UserEntity addressee) {

    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {

    }
}