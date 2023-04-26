package org.example.services;

import org.example.builders.TariffBuilder;
import org.example.dtos.TariffDTO;
import org.example.dtos.TariffUpdateDTO;
import org.example.entities.Field;
import org.example.entities.Tariff;
import org.example.repositories.FieldRepository;
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
public class TariffService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffService.class);

    private final TariffRepository tariffRepository;

    private final FieldRepository fieldRepository;

    @Autowired
    public TariffService(TariffRepository tariffRepository, FieldRepository fieldRepository){
        this.tariffRepository = tariffRepository;
        this.fieldRepository = fieldRepository;
    }

    /**
     * CREATE
     */
    public UUID insert(TariffDTO tariffDTO){
        Optional<Field> field = fieldRepository.findByName(tariffDTO.getFieldName());
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", tariffDTO.getFieldName());
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }

        if (!this.isTariffTypeValid(tariffDTO.getType())){
            LOGGER.error("Tariff type is invalid! It must have one of the following values: Hourly, Daily, Weekly, Monthly");
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }

        Tariff tariff = TariffBuilder.toEntity(tariffDTO);
        tariff.setField(field.get());
        tariffRepository.save(tariff);
        LOGGER.info("Tariff was inserted in db");
        return tariff.getId();
    }

    /**
     * This verifies that the type introduced is one of the 4 valid ones (Hourly, Daily, Weekly, Monthly)
     */
    private boolean isTariffTypeValid(String type){
        return type.equals("Hourly") || type.equals("Daily") || type.equals("Weekly") || type.equals("Monthly");
    }

    /**
     * READ
     */
    public List<TariffDTO> getAllTariffs(){
        List<Tariff> tariffList = tariffRepository.findAll();
        return tariffList.stream().map(TariffBuilder::toTariffDTO).collect(Collectors.toList());
    }

    public TariffDTO getTariffById(UUID id){
        Optional<Tariff> tariff = tariffRepository.findById(id);
        if(!tariff.isPresent()) {
            LOGGER.error("Tariff with id {} was not found", id);
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }
        return TariffBuilder.toTariffDTO(tariff.get());
    }

    public List<TariffDTO> getTariffByField(String fieldName){
        Optional<Field> field = fieldRepository.findByName(fieldName);
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", fieldName);
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }
        List<Tariff> tariff = tariffRepository.findByField(field.get());

        return tariff.stream().map(TariffBuilder::toTariffDTO).collect(Collectors.toList());
    }

    /**
     * UPDATE
     */
    public UUID update(TariffUpdateDTO tariffUpdateDTO){
        Optional<Field> field = fieldRepository.findByName(tariffUpdateDTO.getFieldName());
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", tariffUpdateDTO.getFieldName());
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }

        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), tariffUpdateDTO.getType());
        if(!tariff.isPresent()) {
            LOGGER.error("Tariff was not found in db");
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }

        tariff.get().setPrice(tariffUpdateDTO.getOldPrice());
        tariffRepository.save(tariff.get());
        LOGGER.info("Tariff was set from {} to {}", tariffUpdateDTO.getOldPrice(), tariffUpdateDTO.getNewPrice());
        return tariff.get().getId();
    }

    /**
     * DELETE
     */
    public UUID delete(TariffDTO tariffDTO){
        Optional<Field> field = fieldRepository.findByName(tariffDTO.getFieldName());
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", tariffDTO.getFieldName());
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }

        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), tariffDTO.getType());
        if(!tariff.isPresent()) {
            LOGGER.error("Tariff was not found in db");
            throw new ResourceNotFoundException(TariffService.class.getSimpleName());
        }
        UUID id = tariff.get().getId();
        tariffRepository.delete(tariff.get());
        LOGGER.info("Tariff was deleted");
        return id;
    }

}
