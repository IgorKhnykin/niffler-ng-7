package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {
    private final SpendDao spendDao = new SpendDaoJdbc();

    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(spendDao.create(spendEntity));
    }

    public Optional<SpendJson> findSpendById(UUID id) {
        Optional<SpendEntity> se = spendDao.findSpendById(id);
        return se.map(SpendJson::fromEntity);
    }

    public List<SpendJson> findAllByUsername(String username) {
        return spendDao.findAllByUsername(username)
                .stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public void deleteSpend(SpendJson spendJson) {
        SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
        spendDao.deleteSpend(spendEntity);
    }
}
