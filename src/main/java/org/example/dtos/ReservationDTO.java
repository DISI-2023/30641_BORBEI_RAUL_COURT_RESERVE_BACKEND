package org.example.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {

    private UUID id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private double finalPrice;

    private String fieldName;

    private String userEmail;

    private String type;

}
