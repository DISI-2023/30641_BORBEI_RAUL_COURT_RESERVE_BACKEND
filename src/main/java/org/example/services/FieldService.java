package org.example.services;

import org.example.builders.FieldBuilder;
import org.example.builders.TariffBuilder;
import org.example.dtos.FieldAndLocationDetailsDTO;
import org.example.dtos.FieldDetailsDTO;
import org.example.entities.Field;
import org.example.entities.Location;
import org.example.entities.Tariff;
import org.example.repositories.FieldRepository;
import org.example.repositories.LocationRepository;
import org.example.repositories.TariffRepository;
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

    private final TariffRepository tariffRepository;

    @Autowired
    public FieldService(FieldRepository fieldRepository, LocationRepository locationRepository, TariffRepository tariffRepository) {
        this.fieldRepository = fieldRepository;
        this.locationRepository = locationRepository;
        this.tariffRepository = tariffRepository;
    }

    /** CREATE **/
    /**
     * tested
     **/
    public UUID insert(FieldDetailsDTO dto) {
        //location validation
        Location newLocation = this.validateLocation(dto.getLocationId());

        Field field = FieldBuilder.toEntity(dto, newLocation);
        field = fieldRepository.save(field);
        createDefaultTariffs(field, "Hourly");
        createDefaultTariffs(field, "Daily");
        createDefaultTariffs(field, "Weekly");
        createDefaultTariffs(field, "Monthly");
        LOGGER.debug("Field with id {} was inserted in db", field.getId());
        return field.getId();
    }

    private void createDefaultTariffs(Field field, String type) {
        Tariff newTariff = new Tariff();
        newTariff.setField(field);
        newTariff.setPrice(0);
        newTariff.setType(type);
        tariffRepository.save(newTariff);
    }

    /** SELECT **/
    /**
     * tested
     **/
    public List<FieldAndLocationDetailsDTO> findAll() {
        List<Field> fields = fieldRepository.findAll();
        return fields.stream().map(FieldBuilder::toFieldAndLocationDetailsDTO).collect(Collectors.toList());
    }

    /**
     * tested
     **/
    public FieldDetailsDTO findById(UUID id) {
        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()) {
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        return FieldBuilder.toFieldDetailsDTO(field.get());
    }

    /**
     * tested
     **/
    public List<FieldDetailsDTO> findByLocationId(UUID locationId) {
        Location location = this.validateLocation(locationId);

        List<Field> fields = fieldRepository.findByLocation(location);
        return fields.stream().map(FieldBuilder::toFieldDetailsDTO).collect(Collectors.toList());

    }

    /** UPDATE **/
    /**
     * tested
     **/
    public UUID update(FieldDetailsDTO dto) {
        UUID id = dto.getId();

        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()) {
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        if (dto.getLocationId() != null) {
            Location newLocation = this.validateLocation(dto.getLocationId());
            field.get().setLocation(newLocation);
        }
        if (dto.getName() != null)
            field.get().setName(dto.getName());

        Field updatedField = fieldRepository.save(field.get());
        LOGGER.debug("Field with id {} was updated in db", id);
        return id;
    }

    /** DELETE **/
    /**
     * tested
     **/
    public void delete(UUID id) {
        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()) {
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        fieldRepository.deleteById(id);
        LOGGER.debug("Field with id {} was deleted", id);
    }

    /**
     * This method checks if a location ID (coming usually from a DTO)
     * is valid and can be found in the DB and if it is, the method will
     * return the Location object (to be added to the entity later on)
     */
    private Location validateLocation(UUID locationId) {
        Optional<Location> location = locationRepository.findById(locationId);
        if (!location.isPresent()) {
            LOGGER.error("Location with id {} was not found in db", locationId);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        return location.get();
    }

}
