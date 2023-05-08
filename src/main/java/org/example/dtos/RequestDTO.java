package org.example.dtos;

import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {

    private UUID id;

    private boolean take_over;

    private UUID postedByUserId;

    private UUID takenByUserId;

    private UUID reservationId;
}
