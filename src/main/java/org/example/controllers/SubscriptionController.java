package org.example.controllers;

import org.example.dtos.SubscriptionDTO;
import org.example.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping(value = "/subscription")
public class SubscriptionController {
    public final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Creates a subscription and the recurrent reservations
     * @param subscriptionDTO that includes dayOfWeek (MONDAY, TUESDAY, WEDNESDAY,  THURSDAY, FRIDAY, SATURDAY, SUNDAY)
     *                        startTime, endTime (yyyy-MM-dd)
     *                        startHour, endHor (hh:mm:ss) 0-23h format
     *                        type (Weekly, Monthly, Annually)
     *                        fieldName
     *                        userEmail
     * @return
     */
    @PostMapping()
    public ResponseEntity<UUID> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO){
       UUID id ;
        try{
           id = subscriptionService.insert(subscriptionDTO);
        }catch(ResourceNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch (IllegalArgumentException a){
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<SubscriptionDTO>> readAll(){
        List<SubscriptionDTO> subscriptionDTOS = subscriptionService.getAll();
        if(subscriptionDTOS.isEmpty())
            return new ResponseEntity<>(subscriptionDTOS, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(subscriptionDTOS, HttpStatus.OK);
    }

    /**
     * Gets all subscription of user with the given id
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<List<SubscriptionDTO>> readAllSubscriptionsOfUser(@PathVariable("id") UUID id){
        List<SubscriptionDTO> subscriptionDTOS = subscriptionService.getSubscriptionOfUser(id);
        if(subscriptionDTOS.isEmpty())
            return new ResponseEntity<>(subscriptionDTOS, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(subscriptionDTOS, HttpStatus.OK);
    }
}
