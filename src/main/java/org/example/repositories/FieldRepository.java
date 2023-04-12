package org.example.repositories;

import org.example.entities.Field;
import org.example.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {

    public List<Field> findByLocation(Location location);

}