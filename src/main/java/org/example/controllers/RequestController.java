package org.example.controllers;

import org.example.dtos.RequestDTO;
import org.example.dtos.RequestDetailsDTO;
import org.example.services.RequestService;
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
@RequestMapping(value = "/request")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService){
        this.requestService = requestService;
    }

    /** INSERT
     * not tested
     **/
    // The JSON should contain only 1 field "reservationId" for insertion
    @PostMapping
    public ResponseEntity<UUID> insertRequest(@Valid @RequestBody RequestDTO dto){
        UUID id = requestService.insert(dto);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /** SELECT
     * not tested
     **/
    @GetMapping
    public ResponseEntity<List<RequestDetailsDTO>> getAllRequests(){
        List<RequestDetailsDTO> requests = requestService.findAll();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }
}
