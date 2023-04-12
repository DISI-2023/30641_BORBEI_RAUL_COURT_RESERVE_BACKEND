package org.example.dtos;

import lombok.*;
import org.example.entities.Location;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FieldDetailsDTO {

    /** This has the exact fields as the entity,
     * but on Raul's advice it was not recommended
     * to send the entity directly to the frontend **/

    private UUID id;

    private String name;

    private Location location;
}
