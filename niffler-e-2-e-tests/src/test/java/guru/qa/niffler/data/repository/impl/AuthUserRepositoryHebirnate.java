package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class AuthUserRepositoryHebirnate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.authJdbcUrl());


    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        entityManager.joinTransaction();
        entityManager.persist(authUser);
        return authUser;
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
        return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
    }

    @Override
    public Optional<AuthUserEntity> findUserByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery("select u from AuthUserEntity u where u.username =: username", AuthUserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser(AuthUserEntity authUser) {

    }

    @Override
    public List<AuthUserEntity> findAll() {
        return List.of();
    }
}
