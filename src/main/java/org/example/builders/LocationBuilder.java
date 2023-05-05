package org.example.builders;

import org.example.dtos.LocationDTO;
import org.example.entities.Location;

public class LocationBuilder {

    public static LocationDTO toLocationDTO(Location location){
        return LocationDTO.builder()
                .id(location.getId())
                .name(location.getName())
                .street(location.getStreet())
                .number(location.getNumber())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    public static Location toEntity(LocationDTO dto){
        return Location.builder()
                .id(dto.getId())
                .name(dto.getName())
                .street(dto.getStreet())
                .number(dto.getNumber())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

}
