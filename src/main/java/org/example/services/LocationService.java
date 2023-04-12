package org.example.services;

import org.example.builders.LocationBuilder;
import org.example.dtos.LocationDTO;
import org.example.entities.Location;
import org.example.repositories.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository){
        this.locationRepository = locationRepository;
    }

    /** CREATE **/
    /** not tested **/
    public UUID insert(LocationDTO dto){
        Location location = LocationBuilder.toEntity(dto);
        location = locationRepository.save(location);
        LOGGER.debug("Location with id {} was inserted in db", location.getId());
        return location.getId();
    }

    /** SELECT **/
    /** not tested **/
    public List<LocationDTO> findAll(){
        List<Location> locations = locationRepository.findAll();
        return locations.stream().map(LocationBuilder::toLocationDTO).collect(Collectors.toList());
    }

    /** not tested **/
    public LocationDTO findById(UUID id){
        Optional<Location> location = locationRepository.findById(id);
        if (!location.isPresent()){
            LOGGER.error("Location with id {} was not found in db", id);
            throw new ResourceNotFoundException(Location.class.getSimpleName());
        }
        return LocationBuilder.toLocationDTO(location.get());
    }

    /** UPDATE **/
    /** not tested **/
    public UUID update(LocationDTO dto){
        UUID id = dto.getId();
        Optional<Location> location = locationRepository.findById(id);
        if (!location.isPresent()){
            LOGGER.error("Location with id {} was not found in db", id);
            throw new ResourceNotFoundException(Location.class.getSimpleName());
        }

        location.get().setName(dto.getName());
        location.get().setStreet(dto.getStreet());
        location.get().setNumber(dto.getNumber());
        Location updatedLocation = locationRepository.save(location.get());
        LOGGER.debug("Location with id {} was updated in db", id);
        return id;
    }

    /** DELETE **/
    /** not tested **/
    public void delete(UUID id){
        Optional<Location> location = locationRepository.findById(id);
        if (!location.isPresent()){
            LOGGER.error("Location with id {} was not found in db", id);
            throw new ResourceNotFoundException(Location.class.getSimpleName());
        }
        locationRepository.deleteById(id);
        LOGGER.debug("Location with id {} was deleted", id);
    }

}
