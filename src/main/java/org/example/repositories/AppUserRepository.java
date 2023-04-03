package org.example.repositories;

import org.example.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    public Optional<AppUser> findByEmailAndPassword(String email, String password);
}
