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

    /**
     * The differences between this and the entity itself are the exclusion of the OneToMany fields
     * and the fact that in a DTO only a location ID is provided, instead of a whole Location object
     * **/

    private UUID id;

    private String name;

    private UUID locationId;
}
