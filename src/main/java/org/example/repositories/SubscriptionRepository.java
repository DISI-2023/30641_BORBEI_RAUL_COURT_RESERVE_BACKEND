package org.example.repositories;

import org.example.entities.AppUser;
import org.example.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByAppUser(AppUser appUser);
}
