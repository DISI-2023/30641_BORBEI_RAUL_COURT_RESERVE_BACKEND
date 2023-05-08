package org.example.controllers;

import org.example.dtos.FieldNameAndDateDTO;
import org.example.dtos.FreeReservationIntervalsDTO;
import org.example.dtos.ReservationDTO;
import org.example.services.EmailService;
import org.example.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping(value = "/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    private final EmailService emailService;

    public ReservationController(ReservationService reservationService, EmailService emailService) {
        this.reservationService = reservationService;
        this.emailService = emailService;
    }

    /**
     *
     * @param reservationDTO
     * @return UUID
     * The DTO should contain the following: startTime, endTime, fieldName, userEmail
     * The default type is Hourly and  is not needed to be specified
     */
    @PostMapping()
    public ResponseEntity<UUID> createReservation(@Valid @RequestBody ReservationDTO reservationDTO){
        reservationDTO.setType("Hourly");
        UUID id = reservationService.insert(reservationDTO);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<ReservationDTO>> getAllReservations(){
        List<ReservationDTO> list = reservationService.getAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * Get reservation of user with id {}
     * @param id
     * @return
     */
    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@PathVariable("id") UUID id){
        List<ReservationDTO> reservationDTOS = reservationService.getUserReservations(id);
        return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
    }

    /**
     * Gets a list of all vacant slots in the timetable from a specified Field on a specific date
     * @param fieldNameAndDateDTO
     * @return
     */
    @PostMapping(value="/vacancies")
    public ResponseEntity<List<FreeReservationIntervalsDTO>> getVacantIntervalsByFieldAndDate(
            @Valid @RequestBody FieldNameAndDateDTO fieldNameAndDateDTO){

        List<FreeReservationIntervalsDTO> vacantIntervals = reservationService.getVacantIntervalsByFieldAndDate(
                fieldNameAndDateDTO);

        return new ResponseEntity<>(vacantIntervals, HttpStatus.OK);
    }

    /**
     * Method for updating start and end time of a reservation
     * The final price will be computed again
     * @param reservationDTO
     * @return
     */
    @PutMapping()
    public ResponseEntity<UUID> update(@Valid @RequestBody ReservationDTO reservationDTO){
        UUID id  = reservationService.update(reservationDTO);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    /**
     * @param id
     * @return true OK if deleted or NOT_MODIFIED if not deleted
     */
    @DeleteMapping(value = "/{id}")
    public  ResponseEntity<UUID> deleteReservation(@PathVariable("id") UUID id){
        boolean deleted = reservationService.delete(id);
        if(deleted)
            return new ResponseEntity<>(id, HttpStatus.OK);
        else
            return new ResponseEntity<>(id, HttpStatus.NOT_MODIFIED);
    }

    /**
     *Method for sending email
     * parameter : reservation id
     */
    @GetMapping(value = "/email")
    public ResponseEntity<Boolean> sendReservationEmail(@RequestParam(value = "id") UUID reservationID){
        try {
            emailService.sendReservationEmail(reservationID);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

}
