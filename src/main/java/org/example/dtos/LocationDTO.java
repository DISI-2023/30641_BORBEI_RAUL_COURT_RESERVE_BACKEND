package org.example.dtos;

import lombok.*;

import javax.persistence.Column;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    /** This has the exact fields as the entity (except the OneToMany fields),
     * but on Raul's advice it was not recommended to send the entity directly
     * to the frontend **/

    private UUID id;

    private String name;

    private String street;

    private String number;

}
