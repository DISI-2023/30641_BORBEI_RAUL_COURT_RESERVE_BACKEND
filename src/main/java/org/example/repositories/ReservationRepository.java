package org.example.repositories;

import org.example.entities.Field;
import org.example.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findByFieldAndStartTimeAndEndTime(Field field, LocalDateTime startTime, LocalDateTime endTime);
}
