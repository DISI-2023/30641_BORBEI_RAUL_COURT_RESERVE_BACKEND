package org.example.dtos;

import lombok.*;
import org.example.entities.AppUser;
import org.example.entities.Reservation;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetailsDTO {

    private UUID id;

    private boolean take_over;

    private AppUserDetailsDTO postedByUser;

    private AppUserDetailsDTO takenByUser;

    private ReservationDTO reservation;
}
