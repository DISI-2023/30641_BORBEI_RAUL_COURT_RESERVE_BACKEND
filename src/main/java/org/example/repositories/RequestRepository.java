package org.example.repositories;

import org.example.entities.Request;
import org.example.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {
    Request findFirstByReservation(Reservation reservation);

}
