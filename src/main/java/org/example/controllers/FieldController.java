package org.example.controllers;

import org.example.dtos.FieldAndLocationDetailsDTO;
import org.example.dtos.FieldDetailsDTO;
import org.example.dtos.LocationDTO;
import org.example.services.FieldService;
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
@RequestMapping(value = "/field")
public class FieldController {
    private final FieldService fieldService;

    @Autowired
    public FieldController(FieldService fieldService){
        this.fieldService = fieldService;
    }

    /**
     * I have tested all methods of this class marked with ~tested~ and under normal circumstances
     * locations are inserted, selected, updated and deleted from the DB.
     */

    /** INSERT **/
    /** tested **/
    @PostMapping
    public ResponseEntity<UUID> insertField(@Valid @RequestBody FieldDetailsDTO dto){
        UUID id = fieldService.insert(dto);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /** SELECT **/
    /** tested **/
    @GetMapping
    public ResponseEntity<List<FieldAndLocationDetailsDTO>> getFields(){
        List<FieldAndLocationDetailsDTO> fields = fieldService.findAll();
        return new ResponseEntity<>(fields, HttpStatus.OK);
    }

    /** tested **/
    @GetMapping(value = "/{id}")
    public ResponseEntity<FieldDetailsDTO> getFieldById(@PathVariable("id") UUID id){
        FieldDetailsDTO dto = fieldService.findById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /** tested **/
    @GetMapping(value = "/location/{id}")
    public ResponseEntity<List<FieldDetailsDTO>> getFieldsByLocation(@PathVariable("id") UUID loactionId){
        List<FieldDetailsDTO> fields = fieldService.findByLocationId(loactionId);
        return new ResponseEntity<>(fields, HttpStatus.OK);
    }

    /** tested **/
    @PutMapping
    public ResponseEntity<UUID> updateField(@Valid @RequestBody FieldDetailsDTO dto){
        UUID id = fieldService.update(dto);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    /** tested
     *  If a location is deleted all fields from that location
     *  are deleted too, according to the cascading rules
     **/
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteField(@PathVariable("id") UUID id){
        fieldService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

}
