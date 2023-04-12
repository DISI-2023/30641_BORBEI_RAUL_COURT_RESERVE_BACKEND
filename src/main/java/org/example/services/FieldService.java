package org.example.services;

import org.example.builders.FieldBuilder;
import org.example.dtos.FieldDetailsDTO;
import org.example.entities.Field;
import org.example.entities.Location;
import org.example.repositories.FieldRepository;
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
public class FieldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldService.class);
    private final FieldRepository fieldRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public FieldService (FieldRepository fieldRepository, LocationRepository locationRepository){
        this.fieldRepository = fieldRepository;
        this.locationRepository = locationRepository;
    }

    /** CREATE **/
    /** tested **/
    public UUID insert(FieldDetailsDTO dto){
        //location validation
        Location newLocation = this.validateLocation(dto.getLocationId());

        Field field = FieldBuilder.toEntity(dto, newLocation);
        field = fieldRepository.save(field);

        LOGGER.debug("Field with id {} was inserted in db", field.getId());
        return field.getId();
    }

    /** SELECT **/
    /** tested **/
    public List<FieldDetailsDTO> findAll(){
        List<Field> fields = fieldRepository.findAll();
        return fields.stream().map(FieldBuilder::toFieldDetailsDTO).collect(Collectors.toList());
    }

    /** tested **/
    public FieldDetailsDTO findById(UUID id){
        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()){
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        return FieldBuilder.toFieldDetailsDTO(field.get());
    }

    /** UPDATE **/
    /** tested **/
    public UUID update(FieldDetailsDTO dto){
        UUID id = dto.getId();

        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()){
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }

        Location newLocation = this.validateLocation(dto.getLocationId());

        field.get().setName(dto.getName());
        field.get().setLocation(newLocation);
        Field updatedField = fieldRepository.save(field.get());
        LOGGER.debug("Field with id {} was updated in db", id);
        return id;
    }

    /** DELETE **/
    /** tested **/
    public void delete(UUID id){
        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()){
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        fieldRepository.deleteById(id);
        LOGGER.debug("Field with id {} was deleted", id);
    }

    /** This method checks if a location ID (coming usually from a DTO)
     *  is valid and can be found in the DB and if it is, the method will
     *  return the Location object (to be added to the entity later on)
     */
    private Location validateLocation(UUID locationId){
        Optional<Location> location = locationRepository.findById(locationId);
        if (!location.isPresent()){
            LOGGER.error("Location with id {} was not found in db", locationId);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        return location.get();
    }

}
