package org.example.builders;

import org.example.dtos.FieldDetailsDTO;
import org.example.entities.Field;

public class FieldBuilder {

    public static FieldDetailsDTO toFieldDetailsDTO(Field field){
        return FieldDetailsDTO.builder().id(field.getId()).
                name(field.getName()).
                location(field.getLocation()).build();
    }

    public static Field toEntity(FieldDetailsDTO dto){
        return Field.builder().id(dto.getId()).
                name(dto.getName()).
                location(dto.getLocation()).
                build();
    }
}
