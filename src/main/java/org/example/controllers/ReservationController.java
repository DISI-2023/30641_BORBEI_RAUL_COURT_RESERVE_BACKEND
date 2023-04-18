package org.example.controllers;

import org.example.dtos.ReservationDTO;
import org.example.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping(value = "/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     *
     * @param reservationDTO
     * @return UUID
     * The DTO should contain the following: startTime, endTime, fieldName, userEmail and type ( tariff type)
     */
    @PostMapping()
    public ResponseEntity<UUID> createReservation(@Valid @RequestBody ReservationDTO reservationDTO){
        UUID id = reservationService.insert(reservationDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<ReservationDTO>> getAllReservations(){
        List<ReservationDTO> list = reservationService.getAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
