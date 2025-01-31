package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.FriendshipStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserRepositoryHebirnate implements UserRepository {

    private static final Config CFG = Config.getInstance();

    EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Override
    public void updateUsers(UserEntity requester, UserEntity addressee) {

    }

    @Override
    public UserEntity createUser(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery("select u from UserEntity u where u.username =: username", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult());
        }catch (NoResultException e) {
            return Optional.empty();
        }
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
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee); //todo надо разобраться как работает и переопределение метода equals
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);

    }
}
