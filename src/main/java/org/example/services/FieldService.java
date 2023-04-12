package org.example.services;

import org.example.builders.FieldBuilder;
import org.example.dtos.FieldDetailsDTO;
import org.example.entities.Field;
import org.example.repositories.FieldRepository;
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

    @Autowired
    public FieldService (FieldRepository fieldRepository){
        this.fieldRepository = fieldRepository;
    }

    /** CREATE **/
    /** not tested **/
    public UUID insert(FieldDetailsDTO dto){
        Field field = FieldBuilder.toEntity(dto);
        field = fieldRepository.save(field);
        LOGGER.debug("Field with id {} was inserted in db", field.getId());
        return field.getId();
    }

    /** SELECT **/
    /** not tested **/
    public List<FieldDetailsDTO> findAll(){
        List<Field> fields = fieldRepository.findAll();
        return fields.stream().map(FieldBuilder::toFieldDetailsDTO).collect(Collectors.toList());
    }

    /** not tested **/
    public FieldDetailsDTO findById(UUID id){
        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()){
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        return FieldBuilder.toFieldDetailsDTO(field.get());
    }

    /** UPDATE **/
    /** not tested **/
    public UUID update(FieldDetailsDTO dto){
        UUID id = dto.getId();
        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()){
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }

        field.get().setName(dto.getName());
        field.get().setLocation(dto.getLocation());
        Field updatedField = fieldRepository.save(field.get());
        LOGGER.debug("Field with id {} was updated in db", id);
        return id;
    }

    /** DELETE **/
    /** not tested **/
    public void delete(UUID id){
        Optional<Field> field = fieldRepository.findById(id);
        if (!field.isPresent()){
            LOGGER.error("Field with id {} was not found in db", id);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        fieldRepository.deleteById(id);
        LOGGER.debug("Field with id {} was deleted", id);
    }

}
