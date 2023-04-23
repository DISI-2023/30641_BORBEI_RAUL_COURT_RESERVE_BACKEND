package org.example.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreeReservationIntervalsDTO {

    /**
     * This class is used exclusively to create and send free hourly intervals when there are no reservations made
     * All of these are hourly and have a fixed hour start time and an end time (12:00, 15:00, 21:00, etc.)
     */
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
