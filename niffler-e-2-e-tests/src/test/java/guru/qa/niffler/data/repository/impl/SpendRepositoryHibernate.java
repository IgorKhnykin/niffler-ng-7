package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public @Nonnull SpendEntity createSpend(SpendEntity spend) {
        entityManager.joinTransaction();
        Optional<CategoryEntity> category;
        try {
            category = Optional.of(entityManager.createQuery("select c from CategoryEntity c where c.username =: username and c.name =: name", CategoryEntity.class)
                    .setParameter("username", spend.getCategory().getUsername())
                    .setParameter("name", spend.getCategory().getName())
                    .getSingleResult());
        } catch (NoResultException e) {
            category = Optional.empty();
        }

        if (category.isPresent()) {
            spend.setCategory(category.get());
        } else {
            entityManager.flush();
            entityManager.persist(spend.getCategory());
        }
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public @Nonnull SpendEntity updateSpend(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(entityManager.contains(spend) ? spend : entityManager.merge(spend));
        return spend;
    }

    @Override
    public @Nonnull CategoryEntity updateCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(entityManager.contains(category) ? category : entityManager.merge(category));
        return category;
    }

    @Override
    public @Nonnull CategoryEntity createCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try {
            return Optional.of(entityManager.createQuery("select c from CategoryEntity c where c.username =: username and c.name =: name", CategoryEntity.class)
                    .setParameter("username", username)
                    .setParameter("name", categoryName)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull Optional<SpendEntity> findSpendById(UUID id) {
        return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
    }

    @Override
    public @Nonnull List<SpendEntity> findByUsernameAndSpendDescription(String username, String spendDescription) {
        return entityManager.createQuery("select s from SpendEntity s where s.username =: username and s.description =: description", SpendEntity.class)
                .setParameter("username", username)
                .setParameter("description", spendDescription)
                .getResultList();
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(spend) ? spend : entityManager.merge(spend));
    }

    @Override
    public void removeCategory(CategoryEntity categoryEntity) {
        entityManager.joinTransaction();
        List<SpendEntity> spendEntityList = entityManager.createQuery("select s from SpendEntity s where category.name =: name", SpendEntity.class)
                .setParameter("name", categoryEntity.getName())
                .getResultList();
        if (!spendEntityList.isEmpty()) {
            spendEntityList.forEach(spend -> entityManager.remove(spend));
            entityManager.remove(spendEntityList.get(0).getCategory());
        }
        else {
            Optional<CategoryEntity> categoryFromBack = Optional.ofNullable(entityManager.find(CategoryEntity.class, categoryEntity.getId()));
            categoryFromBack.ifPresent(category -> entityManager.remove(category));
        }
    }

    @Override
    public @Nonnull List<SpendEntity> findAllSpendsByUsername(String username) {
        return entityManager.createQuery("select s from SpendEntity s where s.username =: username", SpendEntity.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Override
    public @Nonnull List<CategoryEntity> findAllCategoriesByUsername(String username) {
        return entityManager.createQuery("select s from CategoryEntity s where s.username =: username", CategoryEntity.class)
                .setParameter("username", username)
                .getResultList();
    }
}
