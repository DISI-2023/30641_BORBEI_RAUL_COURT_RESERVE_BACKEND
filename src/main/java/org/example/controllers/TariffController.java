package org.example.controllers;

import org.example.dtos.TariffDTO;
import org.example.dtos.TariffUpdateDTO;
import org.example.services.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping(value = "/tariff")
public class TariffController {

    private final TariffService tariffService;

    @Autowired
    TariffController(TariffService tariffService){
        this.tariffService = tariffService;
    }

    @PostMapping()
    public ResponseEntity<UUID> insertTariff(@Valid @RequestBody TariffDTO tariffDTO){
        UUID id = tariffService.insert(tariffDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<TariffDTO>> getAll(){
        List<TariffDTO> tariffDTOS = tariffService.getAllTariffs();
        return new ResponseEntity<>(tariffDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TariffDTO> getTariffById(@PathVariable("id") UUID id){
        TariffDTO tariffDTO = tariffService.getTariffById(id);
        return new ResponseEntity<>(tariffDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/field")
    public ResponseEntity<List<TariffDTO>> getTariffsByField(@RequestParam(value = "name") String fieldName){
        List<TariffDTO> tariffDTOS = tariffService.getTariffByField(fieldName);
        return new ResponseEntity<>(tariffDTOS, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<UUID> updateTariff(@Valid @RequestBody TariffUpdateDTO tariffUpdateDTO){
        UUID id = tariffService.update(tariffUpdateDTO);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<UUID> deleteTariff(@Valid @RequestBody TariffDTO tariffDTO){
        UUID id = tariffService.delete(tariffDTO);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
