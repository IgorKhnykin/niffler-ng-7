package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity authUser) {
        entityManager.joinTransaction();
        entityManager.persist(authUser);
        return authUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity authUser) {
        entityManager.joinTransaction();
        entityManager.persist(entityManager.contains(authUser) ? authUser : entityManager.merge(authUser));
        return authUser;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserById(UUID id) {
        return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery("select u from AuthUserEntity u where u.username =: username", AuthUserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity authUser) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(authUser) ? authUser : entityManager.merge(authUser));
    }
}
