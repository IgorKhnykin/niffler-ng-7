package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.impl.UserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserRepositorySpringJdbc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    static UserRepository getInstance() {
        return switch (System.getProperty("repository.impl", "sjdbc")) {
            case "jpa" -> new UserRepositoryHibernate();
            case "sjdbc" -> new UserRepositorySpringJdbc();
            case "jdbc" -> new UserRepositoryJdbc();
            default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
        };
    }

    UserEntity createUser(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    UserEntity updateUser(UserEntity user);

    List<UserEntity> findAll();

    void sendInvitation(UserEntity requester, UserEntity addressee);

    void addFriend(UserEntity requester, UserEntity addressee);

    void remove(UserEntity user);
}
