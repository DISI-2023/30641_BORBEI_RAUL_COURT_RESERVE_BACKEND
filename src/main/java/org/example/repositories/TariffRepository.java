package org.example.repositories;

import org.example.entities.Field;
import org.example.entities.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TariffRepository extends JpaRepository<Tariff, UUID> {
    List<Tariff> findByField(Field field);

    Optional<Tariff> findByFieldAndType(Field field, String type);
}
