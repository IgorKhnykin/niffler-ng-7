package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class UserRepositoryHibernate implements UserRepository {

    private static final Config CFG = Config.getInstance();

    EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Override
    public @Nonnull UserEntity createUser(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery("select u from UserEntity u where u.username =: username", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult());
        }catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull UserEntity updateUser(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(entityManager.contains(user) ? user : entityManager.merge(user));
        return user;
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return entityManager.createQuery("select u from UserEntity u", UserEntity.class).getResultList();
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }

    @Override
    public void remove(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(entityManager.contains(user) ? user : entityManager.merge(user));
    }
}
