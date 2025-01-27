package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    AuthUserEntity create(AuthUserEntity authUser);

    Optional<AuthUserEntity> findUserById(UUID id);

    Optional<AuthUserEntity> findUserByUsername(String username);

    void deleteUser(AuthUserEntity authUser);

    List<AuthUserEntity> findAll();

}
