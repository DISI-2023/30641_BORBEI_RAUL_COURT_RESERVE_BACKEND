package org.example.dtos;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDTO {
    private UUID id;

    private DayOfWeek dayOfWeek;

    private LocalDate startTime;

    private LocalDate endTime;

    private LocalTime startHour;

    private LocalTime endHour;

    private double finalPrice;

    private String type;

    private String  userEmail;

    private String fieldName;
}
