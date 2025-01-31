package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {
    SpendEntity create(SpendEntity spend);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findAllByUsername(String username);

    void deleteSpend(SpendEntity spend);

    List<SpendEntity> findAll();

}
