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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TariffService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffService.class);

    private TariffRepository tariffRepository;

    private FieldRepository fieldRepository;

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
        if(!field.isPresent())
            LOGGER.error("Field with name {} not found", tariffDTO.getFieldName());

        Tariff tariff = TariffBuilder.toEntity(tariffDTO);
        tariff.setField(field.get());
        tariffRepository.save(tariff);
        LOGGER.info("Tariff was inserted in db");
        return tariff.getId();
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
        if(!tariff.isPresent())
            LOGGER.error("Tariff with id {} was not found", id);
        return TariffBuilder.toTariffDTO(tariff.get());
    }

    public TariffDTO getTariffByField(String fieldName){
        Optional<Field> field = fieldRepository.findByName(fieldName);
        if(!field.isPresent())
            LOGGER.error("Field with name {} not found", fieldName);

        Optional<Tariff> tariff = tariffRepository.findByField(field.get());
        if(!tariff.isPresent())
            LOGGER.error("Tariff was not found in db");

        return TariffBuilder.toTariffDTO(tariff.get());
    }

    /**
     * UPDATE
     */
    public UUID update(TariffUpdateDTO tariffUpdateDTO){
        Optional<Field> field = fieldRepository.findByName(tariffUpdateDTO.getFieldName());
        if(!field.isPresent())
            LOGGER.error("Field with name {} not found", tariffUpdateDTO.getFieldName());

        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), tariffUpdateDTO.getType());
        if(!tariff.isPresent())
            LOGGER.error("Tariff was not found in db");

        tariff.get().setPrice(tariffUpdateDTO.getOldPrice());
        tariffRepository.save(tariff.get());
        LOGGER.info("Tariff was set from {} to {}", tariffUpdateDTO.getOldPrice(), tariffUpdateDTO.getNewPrice());
        return tariff.get().getId();
    }

    /**
     * DELETE
     */
    public void delete(TariffDTO tariffDTO){
        Optional<Field> field = fieldRepository.findByName(tariffDTO.getFieldName());
        if(!field.isPresent())
            LOGGER.error("Field with name {} not found", tariffDTO.getFieldName());

        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), tariffDTO.getType());
        if(!tariff.isPresent())
            LOGGER.error("Tariff was not found in db");
        tariffRepository.delete(tariff.get());
        LOGGER.info("Tariff was deleted");
    }

}
