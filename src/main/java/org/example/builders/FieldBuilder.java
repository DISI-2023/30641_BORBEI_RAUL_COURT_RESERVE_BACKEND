package org.example.builders;

import org.example.dtos.FieldDetailsDTO;
import org.example.entities.Field;
import org.example.entities.Location;

public class FieldBuilder {

    public static FieldDetailsDTO toFieldDetailsDTO(Field field){
        return FieldDetailsDTO.builder().id(field.getId()).
                name(field.getName()).
                locationId(field.getLocation().getId()).build();
    }

    public static Field toEntity(FieldDetailsDTO dto, Location location){
        return Field.builder().id(dto.getId()).
                name(dto.getName()).
                location(location).
                build();
    }
}
