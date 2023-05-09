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
     ** The JSON should contain only 1 field "reservationId" for insertion
     **/
    // tested
    @PostMapping
    public ResponseEntity<UUID> insertRequest(@Valid @RequestBody RequestDTO dto){
        UUID id = requestService.insert(dto);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /** SELECT
     * tested
     **/
    @GetMapping
    public ResponseEntity<List<RequestDetailsDTO>> getAllRequests(){
        List<RequestDetailsDTO> requests = requestService.findAll();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /**
     * SELECT all besides one user's requests
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<List<RequestDetailsDTO>> getAllExceptUser(@PathVariable("id") UUID id){
        List<RequestDetailsDTO> requests = requestService.findAllExceptUser(id);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /** UPDATE taken by user
     ** Here the JSON should only contain 2 fields: "id" (of the request) and "takenByUserId"
     **/
    //tested
    @PutMapping(value = "/takenByUpdate")
    public ResponseEntity<UUID> updateRequestWithTakenByUser(@Valid @RequestBody RequestDTO dto){
        UUID id = requestService.updateTakenByUser(dto);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    /**
     * UPDATE on a request that was taken over by a user
     * The JSON should contain the following fields: "id" (of the request), "takenByUserId" and "reservationId"
     */
    @PutMapping(value = "/take")
    public ResponseEntity<UUID> updateRequestAndReservationWithTakenByUser(@Valid @RequestBody RequestDTO dto){
        UUID id = requestService.acceptTakeOverReservation(dto);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }


    /** DELETE by ID **/
    // tested
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteById(@PathVariable("id") UUID id){
        requestService.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    /** DELETE all requested linked to reservations from the past **/
    // tested
    @DeleteMapping(value = "deleteFromPast")
    public ResponseEntity<String> deleteFromPastReservations(){
        requestService.deleteRequestsFromPast();
        return new ResponseEntity<>("Request for reservations from the past deleted successfully", HttpStatus.OK);
    }
}
