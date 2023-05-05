package org.example.dtos;

import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FieldAndLocationDetailsDTO {

    private UUID id;

    private String name;

    private LocationDTO locationDTO;

    private String imageUrl;
}
