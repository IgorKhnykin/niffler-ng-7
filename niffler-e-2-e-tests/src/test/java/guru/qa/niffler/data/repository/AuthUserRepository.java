package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    static AuthUserRepository getInstance() {
       return switch (System.getProperty("repository.impl", "sjdbc")) {
           case "jpa" -> new AuthUserRepositoryHibernate();
           case "sjdbc" -> new AuthUserRepositorySpringJdbc();
           case "jdbc" -> new AuthUserRepositoryJdbc();
           default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
       };
    }


    AuthUserEntity create(AuthUserEntity authUser);

    AuthUserEntity update(AuthUserEntity authUser);

    Optional<AuthUserEntity> findUserById(UUID id);

    Optional<AuthUserEntity> findUserByUsername(String username);

    void remove(AuthUserEntity authUser);

}
