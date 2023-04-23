package org.example.repositories;

import org.example.entities.AppUser;
import org.example.entities.Field;
import org.example.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findByFieldAndStartTimeAndEndTime(Field field, LocalDateTime startTime, LocalDateTime endTime);
    List<Reservation> findByAppUser(AppUser appUser);

    /**
     * This is used to retrieve all reservations from a field during a business day, the dayStart parameter specifying
     * the start hour (date), while the dayEnds the end hour (date) of the range when reservations can be made
     */
    List<Reservation> findByFieldAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(Field field,
                                                                                       LocalDateTime dayStart,
                                                                                       LocalDateTime dayEnds);
}
