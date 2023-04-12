package org.example.controllers;

import org.example.dtos.LocationDTO;
import org.example.services.LocationService;
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
@RequestMapping(value = "/location")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService){
        this.locationService = locationService;
    }

    /** INSERT **/
    /** not tested **/
    @PostMapping
    public ResponseEntity<UUID> insertLocation(@Valid @RequestBody LocationDTO dto){
        UUID id = locationService.insert(dto);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /** SELECT **/
    /** not tested **/
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getLocations(){
        List<LocationDTO> locations = locationService.findAll();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    /** not tested **/
    @GetMapping(value = "/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable("id") UUID id){
        LocationDTO dto = locationService.findById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /** not tested **/
    @PutMapping
    public ResponseEntity<UUID> updateLocation(@Valid @RequestBody LocationDTO dto){
        UUID id = locationService.update(dto);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    /** not tested **/
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteLocation(@PathVariable("id") UUID id){
        locationService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
