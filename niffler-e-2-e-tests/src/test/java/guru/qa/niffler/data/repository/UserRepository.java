package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

public interface UserRepository {

    void updateUsers(UserEntity requester, UserEntity addressee);
}
