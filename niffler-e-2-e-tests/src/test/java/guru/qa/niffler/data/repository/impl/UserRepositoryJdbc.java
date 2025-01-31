package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;

public class UserRepositoryJdbc implements UserRepository {

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();

    @Override
    public void updateUsers(UserEntity requester, UserEntity addressee) {
        UserEntity requesterEntity = userdataUserDao.findByUsername(requester.getUsername()).get();
        UserEntity addresseeEntity = userdataUserDao.findByUsername(addressee.getUsername()).get();
        userdataUserDao.sendFriendshipRequest(requesterEntity, addresseeEntity);
        userdataUserDao.sendFriendshipRequest(addresseeEntity, requesterEntity);
    }
}